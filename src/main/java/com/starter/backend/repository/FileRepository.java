package com.starter.backend.repository;

import com.starter.backend.models.FileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface FileRepository extends R2dbcRepository<FileEntity, UUID> {
    Flux<FileEntity> findByUploadedBy(UUID uploadedBy);
    Flux<FileEntity> findByFileType(String fileType);
}
