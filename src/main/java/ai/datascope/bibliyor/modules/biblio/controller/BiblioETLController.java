package ai.datascope.bibliyor.modules.biblio.controller;

import ai.datascope.bibliyor.modules.biblio.service.BiblioETLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/etl/doi")
    public void etlForDOI(@RequestParam(value = "doi-list", required = false) String[] doiList) {
        String[] doiListFromCode = {
                "10.1002/cpe.8062",
                "10.1002/spe.3317",
                "10.1007/s10619-018-7245-1",
                "10.1007/s11227-023-05123-4",
                "10.1007/s11277-020-07822-0",
                "10.1007/s11761-020-00306-w"
        };
        if (doiList == null || doiList.length == 0) {
            doiList = doiListFromCode;
        }
        biblioETLService.deleteAllVectors();
        biblioETLService.etlForDOIList(doiList);
    }

}
