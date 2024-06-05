package ai.datascope.bibliyor.modules.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/biblio")
public class RagController {

    @Autowired
    private RagService ragService;

    @GetMapping("/rag")
    public AssistantMessage generate(
            @RequestParam(value = "message", defaultValue = "What bike is good for city commuting?") String message) {
        return ragService.retrieve(message);
    }
}
