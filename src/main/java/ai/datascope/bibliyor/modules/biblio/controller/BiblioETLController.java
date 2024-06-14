package ai.datascope.bibliyor.modules.biblio.controller;

import ai.datascope.bibliyor.modules.biblio.service.BiblioETLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/biblio")
public class BiblioETLController {

    @Autowired
    private BiblioETLService biblioETLService;


    @PutMapping("/etl")
    public void etl() {
        biblioETLService.deleteAllVectors();
        biblioETLService.etl();
    }

}
