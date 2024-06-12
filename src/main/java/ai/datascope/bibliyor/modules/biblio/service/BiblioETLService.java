package ai.datascope.bibliyor.modules.biblio.service;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BiblioETLService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private BiblioRepository biblioRepository;

    private final List<Document> documents;

    public BiblioETLService() {
        this.documents = new ArrayList<>();
    }

    @Transactional
    public void extract(){
        log.info("Loading Biblio Documents");
        List<Biblio> biblioList = biblioRepository.findAll();
        biblioList.forEach(biblio -> {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ResearchQuestions", biblio.getRqs());
            metadata.put("Source", biblio.getSource());
            metadata.put("SourceTitle", biblio.getSourceTitle());
            metadata.put("Year", biblio.getYear());
            metadata.put("DocumentType", biblio.getDocumentType());
            String content = "TITLE:" + '\n'
                    + biblio.getTitle() + '\n'
                    + "AUTHORS:" + '\n'
                    + biblio.getAuthors() + '\n'
                    + "ABSTRACT:" + '\n'
                    + biblio.getAbstractContent();
            documents.add(new Document(String.valueOf(biblio.getId()), content, metadata));
        });
    }

    @Transactional
    public void transform(){
        log.info("Transforming pdf into text using LLM");
    }

    @Transactional
    public void load(){
        log.info("Loading Paper Contents");
        vectorStore.add(documents);
        log.info("Embeddings created.");
    }

//    private List<Document> getDocsFromPdf() {
//        var pdfReader = new PagePdfDocumentReader("classpath:/paper1.pdf",
//                PdfDocumentReaderConfig.builder()
//                        .withPageTopMargin(0)
//                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
//                                .withNumberOfTopTextLinesToDelete(0)
//                                .build())
//                        .withPagesPerDocument(1)
//                        .build());
//            return pdfReader.read();
//    }

}
