package ai.datascope.bibliyor.modules.search.controller;


import ai.datascope.bibliyor.modules.search.model.Place;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ai.datascope.bibliyor.modules.search.service.SearchPlacesService;

import java.util.List;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/search")
public class SearchController {

    @Autowired
    private SearchPlacesService searchPlacesService;

    @GetMapping(value = "/places")
    List<Place> searchPlaces(@RequestParam(
            value = "userQuery",
            defaultValue = "I am looking for apartment near Istanbul City.")
                             String userQuery) {
        return searchPlacesService.searchPlaces(userQuery);
    }

}
