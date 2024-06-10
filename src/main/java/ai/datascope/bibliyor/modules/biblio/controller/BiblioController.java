package ai.datascope.bibliyor.modules.biblio.controller;

import ai.datascope.bibliyor.modules.biblio.service.BiblioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/biblio")
public class BiblioController {

    @Autowired
    private BiblioService biblioService;

    @PutMapping("/loadVectorizeAndStore")
    public void loadVectorizeAndStore() {

        biblioService.loadDocuments();
        biblioService.vectorizeAndStore();
    }

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "query", defaultValue = "What is Data Mesh?") String query) {

        List<Document> similarDocuments = biblioService.retrieveSimilarDocuments(query);
        Prompt prompt =  biblioService.preparePrompt(similarDocuments, query);
        return biblioService.askAIModel(prompt).getContent();
    }

}
