package ai.datascope.bibliyor.modules.search.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {
    String name;
    String description;
    Float price;
}
