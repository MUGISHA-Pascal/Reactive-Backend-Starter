package com.starter.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Table("files")
public class FileEntity {
    @Id
    private UUID id;
    private String fileName;
    private String fileType;
    private String filePath;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private UUID uploadedBy;

    public FileEntity() {
        this.id = UUID.randomUUID();
    }

    public FileEntity(String fileName, String fileType, String filePath, Long fileSize, LocalDateTime uploadedAt, UUID uploadedBy) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }
}
