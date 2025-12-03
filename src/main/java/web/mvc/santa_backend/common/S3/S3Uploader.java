package web.mvc.santa_backend.common.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public String uploadFile(MultipartFile file, String path) throws IOException {
        String key = path + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .contentType(file.getContentType())
                        .contentDisposition("inline")
                        .build()
                , RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

}
