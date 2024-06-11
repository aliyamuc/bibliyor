package ai.datascope.bibliyor.modules.biblio.controller;

import ai.datascope.bibliyor.modules.biblio.controller.dto.BiblioDto;
import ai.datascope.bibliyor.modules.biblio.controller.mapper.BibliyorMapper;
import ai.datascope.bibliyor.modules.biblio.service.BiblioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
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

    @GetMapping(value = "/search")
    public List<BiblioDto> search(@RequestParam(value = "userQuery", defaultValue = "Data Mesh") String query,
                                  @RequestParam(value = "similarityThreshold", defaultValue = "0.8") double similarityThreshold,
                                  @RequestParam(value = "topK", defaultValue = "92") int topK,
                                  @RequestParam(value = "GteYear", defaultValue = "2019") int gteYear,
                                  @RequestParam(value = "LteYear", defaultValue = "2024") int lteYear) {

        var b = new FilterExpressionBuilder();
        var exp = b.and(b.gte("Year", gteYear), b.lte("Year", lteYear));
        SearchRequest searchRequest = SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(similarityThreshold)
                .withFilterExpression(exp.build());
        List<Document> retrievedDocuments = biblioService.retrieveSimilarDocuments(searchRequest);
        return retrievedDocuments.stream().map((Document input) -> BibliyorMapper.mapStringToBiblioDto(input.getContent())).toList();
    }

}
