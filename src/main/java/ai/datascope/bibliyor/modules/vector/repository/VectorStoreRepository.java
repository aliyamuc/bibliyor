package ai.datascope.bibliyor.modules.vector.repository;


import ai.datascope.bibliyor.modules.vector.model.VectorStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface VectorStoreRepository extends JpaRepository<VectorStore, UUID> {

    @Modifying
    @Transactional
    @Query(value = "CREATE INDEX IF NOT EXISTS idx_vector_store_embedding ON vector_store USING HNSW (embedding vector_cosine_ops)", nativeQuery = true)
    void createVectorIndex();

    @Modifying
    @Transactional
    @Query("DELETE FROM VectorStore ")
    void deleteAllVectors();

}
