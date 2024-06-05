package ai.datascope.bibliyor.modules.search.service;

import ai.datascope.bibliyor.modules.search.model.Place;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Transactional(readOnly = true)
@Service
public class SearchPlacesService {

    @Autowired
    private ChatModel chatModel;

    @SuppressWarnings("UnnecessaryStringEscape")
    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(
            """
                    You're an assistant who helps to find lodging in Turkey.
                    Suggest three options. Send back a JSON object in the format below.
                    [{\"name\": \"<hotel name>\", \"description\": \"<hotel description>\", \"price\": <hotel price>}]
                    Don't add any other text to the response. Don't add the new line or any other symbols to the response. Send back the raw JSON.
                    """);

    public List<Place> searchPlaces(String query) {
        var userMessage = new UserMessage(query);
        var modelOptions = AzureOpenAiChatOptions.builder()
                .withDeploymentName("gpt4-o")
                .withTemperature(0.7F)
                .build();
        var promptRequest = new Prompt(List.of(SYSTEM_MESSAGE, userMessage), modelOptions);
        var response = chatModel.call(promptRequest);
        String rawJson = response.getResult().getOutput().getContent();
        Place[] places = new Gson().fromJson(rawJson, Place[].class);
        return List.of(places);
    }
}
