package ai.datascope.bibliyor.modules.chat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/biblio")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/chat")
    public String chat(String userQuery) {
        return chatService.generate(userQuery);
    }

    @GetMapping(value = "/stream")
    public void stream(String userQuery) {
        chatService.stream(userQuery);
    }
}
