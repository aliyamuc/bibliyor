package ai.datascope.bibliyor.modules.etl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/biblio")
public class ETLController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/load")
    public ResponseEntity<Boolean> loadBiblio() {
        try {
            Boolean result = excelService.loadExcelFile();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
