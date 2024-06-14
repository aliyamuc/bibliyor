package ai.datascope.bibliyor.modules.biblio.service;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import ai.datascope.bibliyor.modules.vector.service.VectorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.ai.document.Document;
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

    @Value("${biblio.pdf.dir}")
    private String sourcePdfDir;


    @Transactional
    public void deleteAllVectors(){
        vectorService.deleteAllVectors();
    }

    @Transactional
    public void etl(){
        log.info("Extract, Transform and Loading....");
        List<Biblio> biblioList = biblioRepository.findAll();
        for(Biblio biblio : biblioList){
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ResearchQuestions", biblio.getRqs());
            metadata.put("Source", biblio.getSource());
            metadata.put("SourceTitle", biblio.getSourceTitle());
            metadata.put("Year", biblio.getYear());
            metadata.put("DocumentType", biblio.getDocumentType());

            String paperFilePdf = replaceSlashes(biblio.getDOI()).concat(".pdf");

            String strPdfContent = this.loadPdf(paperFilePdf);

            String strContent = "TITLE:"+"\n"+biblio.getTitle()
                    +"\n"+"AUTHORS:"+"\n"+biblio.getAuthors()
                    +"\n"+"ABSTRACT:"+"\n"+biblio.getAbstractContent()
                    +"\n"+"PDF_CONTENT:"+"\n"+strPdfContent;

            var newDoc = new Document(String.valueOf(biblio.getId()), strContent, metadata);
            vectorStore.write(List.of(newDoc));
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
            //PDFTextStripper pdfStripper = new PDFTextStripper();
            //String text = pdfStripper.getText(document);
            //sb.append("\nContent:\n").append(text);
            document.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sb.toString();
    }
}
