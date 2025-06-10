package com.starter.backend.repository;

import com.starter.backend.models.File;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface FileRepository extends R2dbcRepository<File, UUID> {
    Flux<File> findByUploadedBy(UUID uploadedBy);
    Flux<File> findByFileType(String fileType);
}
