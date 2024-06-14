package ai.datascope.bibliyor.modules.biblio.service;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import ai.datascope.bibliyor.modules.vector.service.VectorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Transactional
public class BiblioETLService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private BiblioRepository biblioRepository;

    @Autowired
    private VectorService vectorService;

    @Autowired
    private PdfChunker pdfChunker;

    @Value("${biblio.pdf.dir}")
    private String sourcePdfDir;


    @Transactional
    public void deleteAllVectors(){
        vectorService.deleteAllVectors();
    }
    @Transactional
    public void etlForOne(){
        Biblio biblio = biblioRepository.findByDOI("10.1002/cpe.8062").orElse(null);

        if(biblio != null){
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ResearchQuestions", biblio.getRqs());
            metadata.put("SourceTitle", biblio.getSourceTitle());
            metadata.put("Year", biblio.getYear());
            metadata.put("DocumentType", biblio.getDocumentType());
            vectorStore.write(this.loadText(metadata));
        }
    }

    @Transactional
    public void etl(){
        log.info("Extract, Transform and Loading....");
        List<Biblio> biblioList = biblioRepository.findAll();
        int counter = 0;
        for(Biblio biblio : biblioList){
            counter++;
            log.info("counter: {}",counter);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ResearchQuestions", biblio.getRqs());
            metadata.put("Source", biblio.getSource());
            metadata.put("SourceTitle", biblio.getSourceTitle());
            metadata.put("Year", biblio.getYear());
            metadata.put("DocumentType", biblio.getDocumentType());

            String paperFilePdf = replaceSlashes(biblio.getDOI()).concat(".pdf");

//            String strPdfContent = this.loadPdf(paperFilePdf);
//
//            String strContent = "TITLE:"+"\n"+biblio.getTitle()
//                    +"\n"+"AUTHORS:"+"\n"+biblio.getAuthors()
//                    +"\n"+"ABSTRACT:"+"\n"+biblio.getAbstractContent()
//                    +"\n"+"PDF_CONTENT:"+"\n"+strPdfContent;
//
//
//
//            var newDoc = new Document(String.valueOf(biblio.getId()), strContent, metadata);
//            vectorStore.write(List.of(newDoc));


            String strPdfContent = this.loadPdf(paperFilePdf);
            List<String> chunks = pdfChunker.chunkText(strPdfContent);

            for (String chunk : chunks) {
                String chunkContent = "TITLE:" + "\n" + biblio.getTitle()
                        + "\n" + "AUTHORS:" + "\n" + biblio.getAuthors()
                        + "\n" + "ABSTRACT:" + "\n" + biblio.getAbstractContent()
                        + "\n" + "PDF_CONTENT:" + "\n" + chunk;

                var newDoc = new Document(chunkContent, metadata);
                vectorStore.write(List.of(newDoc));
            }
        }
    }

    private String replaceSlashes(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("/", "_");
    }

    private String loadPdf(String fileName){
        StringBuilder sb = new StringBuilder();
        File pdfFile = new File(sourcePdfDir.concat("/"+fileName));
        try (InputStream inputStream = new FileInputStream(pdfFile)) {

            PDDocument document = PDDocument.load(inputStream, "", MemoryUsageSetting.setupMainMemoryOnly());
            sb.append(document.getDocumentInformation().getTitle()).append("\n");
            sb.append(document.getDocumentInformation().getAuthor()).append("\n");
            sb.append(document.getDocumentInformation().getKeywords()).append("\n");
            sb.append(document.getDocumentInformation().getSubject()).append("\n");
            // Extract text content
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            sb.append(text);
            document.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sb.toString();
    }

    private List<Document> loadText(Map<String, Object> metadata) {
        TextReader textReader = new TextReader("classpath:data/10.1002_cpe.8062.txt");
        textReader.getCustomMetadata().putAll(metadata);
        return textReader.read();
    }
}
