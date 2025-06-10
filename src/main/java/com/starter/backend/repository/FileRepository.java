package com.starter.backend.repository;

import com.starter.backend.models.File;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface FileRepository extends R2dbcRepository<File, UUID> {
    Mono<File> findByFileName(String fileName);
}
