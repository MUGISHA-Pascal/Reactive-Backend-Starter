package com.starter.backend.services;

import com.starter.backend.exceptions.ResourceNotFoundException;
import com.starter.backend.models.FileEntity;
import com.starter.backend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FIleService {
    private final FileRepository fileRepository;

    public Mono<FileEntity> storeFile(String fileName, String fileType, String filePath, Long fileSize, UUID uploadedBy) {
        FileEntity newFile = new FileEntity();
        newFile.setFileName(fileName);
        newFile.setFileType(fileType);
        newFile.setFilePath(filePath);
        newFile.setFileSize(fileSize);
        newFile.setUploadedBy(uploadedBy);
        newFile.setUploadedAt(LocalDateTime.now());
        return fileRepository.save(newFile);
    }

    public Mono<FileEntity> getFile(UUID id) {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("File", "id", id.toString())));
    }

    public Mono<FileEntity> getFileByName(String fileName) {
        return fileRepository.findAll()
            .filter(file -> file.getFileName().equals(fileName))
            .next()
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("File", "name", fileName)));
    }

    public Mono<Void> deleteFile(UUID id) {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("File", "id", id.toString())))
            .flatMap(fileRepository::delete);
    }
} 