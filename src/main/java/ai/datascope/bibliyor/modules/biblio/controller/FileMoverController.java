package ai.datascope.bibliyor.modules.biblio.controller;

import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ai.datascope.bibliyor.modules.biblio.model.Biblio;


import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/biblio")
public class FileMoverController {

    @Value("${biblio.target.dir}")
    private String targetDir;

    @Autowired
    private BiblioRepository biblioRepository;

    @PostMapping("/move")
    public ResponseEntity<String> moveFiles() {
        List<Biblio> bibloList = biblioRepository.findAll();
        List<String> errorMessages = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(1);

        bibloList.forEach(biblio -> {
            Path targetFile = Paths.get(targetDir, "M-"+ counter.get() +"-"+ biblio.getId() + ".txt");
            counter.set(counter.get() + 1);
            try {
                if (!Files.exists(targetFile)) {
                    Files.createFile(targetFile);
                }
                String content = biblio.getTitle()+"\n" + biblio.getDOI()+"\n";
                Files.writeString(targetFile, content);
            } catch (IOException e) {
                errorMessages.add("Failed to create file: " + targetFile + " due to " + e.getMessage());
            }
        });

        if (!errorMessages.isEmpty()) {
            return ResponseEntity.badRequest().body(String.join("\n", errorMessages));
        }

        return ResponseEntity.ok("Files created successfully.");
    }
}
