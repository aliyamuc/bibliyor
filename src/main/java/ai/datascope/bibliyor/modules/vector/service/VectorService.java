package ai.datascope.bibliyor.modules.vector.service;

import ai.datascope.bibliyor.modules.vector.repository.VectorStoreRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class VectorService {

    @Autowired
    private VectorStoreRepository vectorStoreRepository;

    @PostConstruct
    public void init() {
        vectorStoreRepository.createVectorIndex();
    }
}
