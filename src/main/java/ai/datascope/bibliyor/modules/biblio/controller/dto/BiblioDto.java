package ai.datascope.bibliyor.modules.biblio.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiblioDto {
    String title;
    String authors;
    String abstractContent;
}
