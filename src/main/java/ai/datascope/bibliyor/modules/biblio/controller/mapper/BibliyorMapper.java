package ai.datascope.bibliyor.modules.biblio.controller.mapper;

import ai.datascope.bibliyor.modules.biblio.controller.dto.BiblioDto;
import org.springframework.ai.document.Document;

public class BibliyorMapper {

    @SuppressWarnings("unused")
    public static BiblioDto mapStringToBiblioDto(String input) {

        String[] sections = input.split("(?=TITLE:|AUTHORS:|ABSTRACT:|PDF_CONTENT:)");
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

    public static BiblioDto mapDocumentToBiblioDto(Document input) {

        String title = input.getMetadata().get("Title").toString();
        String sourceTitle = input.getMetadata().get("SourceTitle").toString();
        String year = input.getMetadata().get("Year").toString();
        String documentType = input.getMetadata().get("DocumentType").toString();
        String authors =input.getMetadata().get("Authors").toString();
        String abstractContent = input.getMetadata().get("AbstractContent").toString();

        return BiblioDto.builder()
                .title(title)
                .sourceTitle(sourceTitle)
                .year(year)
                .documentType(documentType)
                .authors(authors)
                .abstractContent(abstractContent)
                .build();
    }
}
