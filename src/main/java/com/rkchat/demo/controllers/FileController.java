package com.rkchat.demo.controllers;


import  com.rkchat.demo.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController {
    private final S3Service s3Service;



    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            // Generate a unique filename (UUID + original extension) to prevent collisions in S3
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Upload the file to S3 using the S3Service
            String fileUrl = s3Service.uploadFile(file, uniqueFileName);

            return ResponseEntity.ok(fileUrl); // Return the S3 public URL
        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }
}

