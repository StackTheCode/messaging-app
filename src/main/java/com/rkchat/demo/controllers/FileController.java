package com.rkchat.demo.controllers;


import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileController () throws IOException {
        Files.createDirectories(this.fileStorageLocation);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Generate a unique filename to prevent collisions and security issues
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString() + "-" + originalFileName;

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(fileUrl);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not store file " + file.getOriginalFilename());
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<UrlResource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            System.out.println("Attempting to serve file from path: " + filePath.toAbsolutePath()); // Debug path
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                System.out.println("File exists. Attempting to probe content type for: " + filePath.getFileName()); // Debug existence
                try {
                    String contentType = Files.probeContentType(filePath); // This is where the IOException occurs
                    if(contentType == null) {
                        contentType = "application/octet-stream";
                    }
                    System.out.println("Content type probed: " + contentType); // Debug success
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } catch (IOException ioException) {
                    System.err.println("IOException while probing content type for " + filePath.getFileName() + ": " + ioException.getMessage());
                    // Re-throw or return appropriate error if probing fails
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                System.out.println("File not found: " + filePath.getFileName()); // Debug not found
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            System.err.println("Malformed URL exception for filename: " + fileName + ": " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

