package com.rkchat.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL; // For public read access
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String s3UrlBase; // Base URL for constructing file URLs

    /**
     * Constructor for S3Service, injecting S3 configuration values.
     * @param accessKey AWS Access Key ID.
     * @param secretKey AWS Secret Access Key.
     * @param region AWS Region.
     * @param bucketName S3 Bucket Name.
     */
    public S3Service(
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket.name}") String bucketName) {

        this.bucketName = bucketName;

        this.s3UrlBase = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);


        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Uploads a MultipartFile to S3 and returns its public URL.
     * @param file The MultipartFile to upload.
     * @param uniqueFileName The unique key (path + filename) for the object in S3.
     * @return The public URL of the uploaded file.
     * @throws IOException If there's an issue reading the file's input stream.
     */
    public String uploadFile(MultipartFile file, String uniqueFileName) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
//
                
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));


        return s3UrlBase + uniqueFileName;
    }

    /**
     * Constructs the public URL for a file already stored in S3.
     * This method is useful if you just have the file name (key) and need its full URL.
     * @param fileName The key of the object in S3.
     * @return The public URL of the file.
     */
    public String getUrl(String fileName) {
        return s3UrlBase + fileName;
    }
}
