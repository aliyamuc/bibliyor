package ai.datascope.bibliyor.modules.biblio.service;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class BiblioService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private BiblioRepository biblioRepository;

    @Value("classpath:/prompts/biblio.st")
    private Resource biblioPrompt;

    private final List<Document> documents;

    public BiblioService() {
        this.documents = new ArrayList<>();
    }

    @Transactional
    public void loadDocuments(){
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
            Document document = new  Document(String.valueOf(biblio.getId()), content, metadata);
            documents.add(document);
        });
    }

    @Transactional
    public void vectorizeAndStore(){
        // Step 2 - Create embeddings and save to vector store
        log.info("Creating Embeddings...");
        // Add the documents to PGVector
        vectorStore.add(documents);
        log.info("Embeddings created.");
    }

    @Transactional
    public List<Document> retrieveSimilarDocuments(String query){
        // Step 3 retrieve related documents to query
        log.info("Retrieving relevant documents");
        List<Document> similarDocuments = vectorStore.similaritySearch(query);
        log.info(String.format("Found %s relevant documents.", similarDocuments.size()));
        return similarDocuments;
    }

    @Transactional
    public List<Document> retrieveSimilarDocuments(SearchRequest searchRequest){
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        log.info(String.format("Found %s relevant documents.", similarDocuments.size()));
        return similarDocuments;
    }

    @Transactional
    public Prompt preparePrompt(List<Document> similarDocuments, String userQuery) {
        var azureModelOptions = AzureOpenAiChatOptions.builder()
                .withDeploymentName("gpt4-o")
                .withTemperature(0.7F)
                .build();

        return new Prompt(List.of(getSystemMessage(similarDocuments),
                new UserMessage(userQuery)),
                azureModelOptions);
    }

    @Transactional
    public AssistantMessage askAIModel(Prompt prompt){
        ChatResponse chatResponse = chatModel.call(prompt);
        log.info("AI responded.");
        return chatResponse.getResult().getOutput();
    }

    private Message getSystemMessage(List<Document> similarDocuments) {
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(biblioPrompt);
        return systemPromptTemplate.createMessage(Map.of("documents", documents));
    }
}
