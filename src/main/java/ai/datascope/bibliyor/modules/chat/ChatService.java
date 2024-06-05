package ai.datascope.bibliyor.modules.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional(readOnly = true)
@Service
public class ChatService {

    @Autowired
    private ChatModel chatModel;

    public String generate(String query) {
        var userMessage = new UserMessage(query);
        var modelOptions = AzureOpenAiChatOptions.builder()
                .withDeploymentName("gpt4-o")
                .withTemperature(0.7F)
                .build();
        var promptRequest = new Prompt(userMessage, modelOptions);
        var response = chatModel.call(promptRequest);
        return response.getResult().getOutput().getContent();
    }
}
