package ai.datascope.bibliyor.modules.biblio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.AssistantMessage;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class BiblioRAGService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private VectorStore vectorStore;


    @Value("classpath:/prompts/biblio.st")
    private Resource biblioPrompt;


    @Transactional
    public List<Document> retrieve(String query){
        log.info("Retrieving relevant documents");
        List<Document> similarDocuments = vectorStore.similaritySearch(query);
        log.info(String.format("Found %s relevant documents.", similarDocuments.size()));
        return similarDocuments;
    }

    @Transactional
    public List<Document> retrieve(SearchRequest searchRequest){
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
        log.info(String.format("Found %s relevant documents.", similarDocuments.size()));
        return similarDocuments;
    }

    @Transactional
    public Prompt augment(List<Document> similarDocuments, String userQuery) {

        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        var systemPromptTemplate = new SystemPromptTemplate(biblioPrompt);
        var systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
        var userMessage = new UserMessage(userQuery);

        var azureModelOptions = AzureOpenAiChatOptions.builder()
                .withDeploymentName("gpt4-o")
                .withTemperature(0.7F)
                .build();

        return new Prompt(List.of(systemMessage, userMessage),azureModelOptions);
    }

    @Transactional
    public AssistantMessage generation(Prompt prompt){
        ChatResponse chatResponse = chatModel.call(prompt);
        log.info("AI Model responded.");
        return chatResponse.getResult().getOutput();
    }
}
