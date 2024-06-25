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
                "10.1007/s11761-020-00306-w",
                "10.1016/j.bdr.2021.100209",
                "10.1016/j.future.2022.07.008",
                "10.1016/j.iot.2021.100432",
                "10.1016/j.jpdc.2022.12.002",
                "10.1016/j.procs.2019.11.267",
                "10.1016/j.procs.2020.03.137",
                "10.1016/j.procs.2021.01.287",
                "10.1016/j.simpat.2022.102709",
                "10.1088/1742-6596/1616/1/012092",
                "10.1107/S1600577524002637",
                "10.1109/ACCESS.2019.2960516",
                "10.1109/ACCESS.2020.3005638",
                "10.1109/ACCESS.2022.3164393",
                "10.1109/ACFPE56003.2022.9952265",
                "10.1109/AEMCSE50948.2020.00100",
                "10.1109/ATIT58178.2022.10024198",
                "10.1109/BigData47090.2019.9005651",
                "10.1109/BigData47090.2019.9006584",
                "10.1109/BigData52589.2021.9671891",
                "10.1109/BigData52589.2021.9671978",
                "10.1109/CBASE60015.2023.10439104",
                "10.1109/CBMS52027.2021.00078",
                "10.1109/CCWC57344.2023.10099195",
                "10.1109/CCWC60891.2024.10427717",
                "10.1109/CLOUD49709.2020.00051",
                "10.1109/CLOUD55607.2022.00051",
                "10.1109/CloudCom59040.2023.00049",
                "10.1109/EDPC53547.2021.9684194",
                "10.1109/ICASSPW59220.2023.10193222",
                "10.1109/ICBASE51474.2020.00056",
                "10.1109/ICCA59364.2023.10401515",
                "10.1109/ICECCME57830.2023.10252220",
                "10.1109/ICFC.2019.00024",
                "10.1109/ICPICS52425.2021.9524169",
                "10.1109/ICSP54964.2022.9778704",
                "10.1109/ICUFN57995.2023.10201181",
                "10.1109/IDAACS58523.2023.10348677",
                "10.1109/IoTaIS60147.2023.10346079",
                "10.1109/IPEC51340.2021.9421164",
                "10.1109/ISC246665.2019.9071708",
        };
        if (doiList == null || doiList.length == 0) {
            doiList = doiListFromCode;
        }
        biblioETLService.deleteAllVectors();
        biblioETLService.etlForDOIList(doiList);
    }

}
