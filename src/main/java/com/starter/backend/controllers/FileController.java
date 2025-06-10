package com.starter.backend.controllers;

import com.starter.backend.exceptions.ResourceNotFoundException;
import com.starter.backend.models.File;
import com.starter.backend.payload.response.Response;
import com.starter.backend.services.FIleService;
import com.starter.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FIleService fileService;
    private final UserService userService;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @PostMapping("/upload")
    public Mono<ResponseEntity<Response>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String fileType) {
        return userService.getLoggedInUser()
            .flatMap(user -> {
                try {
                    // Create uploads directory if it doesn't exist
                    Files.createDirectories(fileStorageLocation);
                    
                    // Generate unique filename
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path targetLocation = fileStorageLocation.resolve(fileName);
                    
                    // Save file to disk
                    Files.copy(file.getInputStream(), targetLocation);
                    
                    // Save file metadata to database
                    return fileService.storeFile(
                        fileName,
                        fileType,
                        targetLocation.toString(),
                        file.getSize(),
                        user.getId()
                    ).map(savedFile -> ResponseEntity.ok(new Response("File uploaded successfully!")));
                } catch (IOException e) {
                    return Mono.just(ResponseEntity.badRequest()
                        .body(new Response("Could not upload file: " + e.getMessage())));
                }
            });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Resource>> getFile(@PathVariable UUID id) {
        return fileService.getFile(id)
            .flatMap(file -> {
                try {
                    Path filePath = Paths.get(file.getFilePath());
                    Resource resource = new UrlResource(filePath.toUri());
                    
                    if (resource.exists() && resource.isReadable()) {
                        return Mono.just(ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(file.getFileType()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + file.getFileName() + "\"")
                            .body(resource));
                    } else {
                        return Mono.error(new ResourceNotFoundException("File", "id", id.toString()));
                    }
                } catch (MalformedURLException e) {
                    return Mono.error(new RuntimeException("Could not read file: " + e.getMessage()));
                }
            });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Response>> deleteFile(@PathVariable UUID id) {
        return fileService.getFile(id)
            .flatMap(file -> {
                try {
                    Path filePath = Paths.get(file.getFilePath());
                    Files.deleteIfExists(filePath);
                    return fileService.deleteFile(id)
                        .then(Mono.just(ResponseEntity.ok(new Response("File deleted successfully!"))));
                } catch (IOException e) {
                    return Mono.just(ResponseEntity.badRequest()
                        .body(new Response("Could not delete file: " + e.getMessage())));
                }
            });
    }
}
