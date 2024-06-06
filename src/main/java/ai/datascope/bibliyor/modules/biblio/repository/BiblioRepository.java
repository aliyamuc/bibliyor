package ai.datascope.bibliyor.modules.biblio.repository;

import ai.datascope.bibliyor.modules.biblio.model.Biblio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiblioRepository extends JpaRepository<Biblio, Long> {


}
