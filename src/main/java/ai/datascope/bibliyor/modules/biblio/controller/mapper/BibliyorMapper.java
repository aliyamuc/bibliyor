package ai.datascope.bibliyor.modules.biblio.controller.mapper;

import ai.datascope.bibliyor.modules.biblio.controller.dto.BiblioDto;

public class BibliyorMapper {

    public static BiblioDto mapStringToBiblioDto(String input) {

        String[] sections = input.split("(?=TITLE:|AUTHORS:|ABSTRACT:)");
        String title = "";
        String authors = "";
        String abstractContent = "";

        for (String section : sections) {
            if (section.startsWith("TITLE:")) {
                title = section.replace("TITLE:", "").trim();
            } else if (section.startsWith("AUTHORS:")) {
                authors = section.replace("AUTHORS:", "").trim();
            } else if (section.startsWith("ABSTRACT:")) {
                abstractContent = section.replace("ABSTRACT:", "").trim();
            }
        }

        return BiblioDto.builder()
                .title(title)
                .authors(authors)
                .abstractContent(abstractContent)
                .build();
    }
}
