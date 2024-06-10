package ai.datascope.bibliyor.modules.biblio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "biblio")
public class Biblio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 510)
    private String authors;
    @Column(length = 510)
    private String authorFullNames;
    @Column(length = 510)
    private String authorsID;
    private String title;
    private Integer year;

    @NotNull
    @Column(unique = true)
    private String DOI;

    private String sourceTitle;
    private String volume;
    private String issue;
    private String artNo;
    private Integer pageStart;
    private Integer pageEnd;
    private Integer pageCount;
    private Integer citedBy;
    @Column(length = 510)
    private String link;
    private String documentType;
    private String publicationStage;
    private String openAccess;
    private String source;
    private String EID;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<ResearchQuestion> rqs = new ArrayList<>();

}
