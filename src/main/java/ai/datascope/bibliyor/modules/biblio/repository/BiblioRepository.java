package ai.datascope.bibliyor.modules.biblio.repository;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BiblioRepository extends JpaRepository<Biblio, Long> {

    Optional<Biblio> findByDOI(String doi);
}
