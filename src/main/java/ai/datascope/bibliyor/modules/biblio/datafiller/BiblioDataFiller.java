package ai.datascope.bibliyor.modules.biblio.datafiller;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import ai.datascope.bibliyor.modules.biblio.repository.BiblioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(10)
public class BiblioDataFiller implements CommandLineRunner {

    @Autowired
    private BiblioRepository biblioRepository;

    @Override
    public void run(String... args) {
        Biblio biblio = new Biblio();
        biblio.setKey("key1");
        biblio.setValue("val1");
        biblioRepository.save(biblio);
    }

}
