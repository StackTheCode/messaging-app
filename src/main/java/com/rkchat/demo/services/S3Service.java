package com.rkchat.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String s3UrlBase;

    /**
     * Constructor for S3Service.
     * It now relies on the AWS SDK's default credential provider chain,
     * which will automatically find credentials from IAM roles, environment variables, etc.
     *
     * @param region AWS Region.
     * @param bucketName S3 Bucket Name.
     */
    public S3Service(
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket.name}") String bucketName) {

        this.bucketName = bucketName;
        // Construct the S3 base URL dynamically.
        // Note: For custom domains or different S3 endpoint configurations, this might need adjustment.
        this.s3UrlBase = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);

        // Initialize S3Client. The SDK will automatically use the default credential provider chain.
        // This chain looks for credentials in the following order (simplified):
        // 1. Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
        // 2. JVM system properties
        // 3. Web Identity Token credentials (for K8s, EKS)
        // 4. Shared credential files (~/.aws/credentials)
        // 5. ECS Container credentials
        // 6. EC2 Instance profile credentials (IAM Role)
        // By removing StaticCredentialsProvider, you allow the SDK to handle credential discovery securely.
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                // No explicit .credentialsProvider() call is needed when using IAM roles
                // or environment variables, as the default provider chain handles it.
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
                .build();

        // Using RequestBody.fromInputStream for efficient streaming of the file
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