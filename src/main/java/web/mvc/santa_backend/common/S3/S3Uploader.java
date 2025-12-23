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

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String uploadFile(MultipartFile file, String path) throws IOException {
        String key = path + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        log.info("========== S3 Upload Start ==========");
        log.info("Bucket: {}", bucketName);
        log.info("Region: {}", region);
        log.info("Key (File Path): {}", key);
        log.info("File Size: {} bytes", file.getSize());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .contentDisposition("inline")
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            
            log.info("========== S3 Upload Success ==========");

        } catch (S3Exception e) {
            log.error("!!! AWS S3 Error Occurred !!!");
            log.error("HTTP Status Code: {}", e.statusCode());
            log.error("AWS Error Code: {}", e.awsErrorDetails().errorCode());
            log.error("Error Message: {}", e.awsErrorDetails().errorMessage());
            throw e;

        } catch (Exception e) {
            log.error("!!! Unknown Error Occurred !!!", e);
            throw new IOException("S3 Upload Failed", e);
        }

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }
}
