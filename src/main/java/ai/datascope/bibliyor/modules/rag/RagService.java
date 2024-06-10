package ai.datascope.bibliyor.modules.rag;

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
import org.springframework.ai.reader.JsonReader;
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
@Transactional(readOnly = true)
@Service
public class RagService {

    @Autowired
    private VectorStore vectorStore;


    @Autowired
    private ChatModel chatModel;

    @Value("classpath:/data/bikes.json")
    private Resource bikesResource;

    @Value("classpath:/prompts/system-qa.st")
    private Resource systemBikePrompt;

    private List<Document> documents;

    @Transactional
    public AssistantMessage ragOnBiblio(String query){
            this.loadDocuments();
            this.vectorizeAndStore();
            List<Document> similarDocuments = this.retrieveSimilarDocuments(query);
            Prompt prompt =  this.preparePrompt(similarDocuments, query);
            return this.askAIModel(prompt);
    }

    private void loadDocuments(){
        // Step 1 - Load JSON document as Documents
        log.info("Loading JSON as Documents");
        JsonReader jsonReader = new JsonReader(bikesResource, "name", "price", "shortDescription", "description");
        this.documents = jsonReader.get();
    }

    private void vectorizeAndStore(){
        // Step 2 - Create embeddings and save to vector store
        log.info("Creating Embeddings...");
        // Add the documents to PGVector
        vectorStore.add(documents);
        log.info("Embeddings created.");
    }

    private List<Document> retrieveSimilarDocuments(String query){
        // Step 3 retrieve related documents to query
        log.info("Retrieving relevant documents");
        List<Document> similarDocuments = vectorStore.similaritySearch(query);
        log.info(String.format("Found %s relevant documents.", similarDocuments.size()));
        return similarDocuments;
    }

    private Prompt preparePrompt(List<Document> similarDocuments,String userQuery) {
        // Step 4 Embed documents into SystemMessage with the `system-qa.st` prompt template
        Message systemMessage = getSystemMessage(similarDocuments);
        UserMessage userMessage = new UserMessage(userQuery);

        // Step 4 - Ask the AI model
        log.info("Asking AI model to reply to question.");
        var azureModelOptions = AzureOpenAiChatOptions.builder()
                .withDeploymentName("gpt4-o")
                .withTemperature(0.7F)
                .build();
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage),azureModelOptions);
        log.info(prompt.toString());
        return prompt;
    }

    private AssistantMessage askAIModel(Prompt prompt){
        ChatResponse chatResponse = chatModel.call(prompt);
        log.info("AI responded.");
        log.info(chatResponse.getResult().getOutput().getContent());
        return chatResponse.getResult().getOutput();
    }

    private Message getSystemMessage(List<Document> similarDocuments) {
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemBikePrompt);
        return systemPromptTemplate.createMessage(Map.of("documents", documents));
    }
}
