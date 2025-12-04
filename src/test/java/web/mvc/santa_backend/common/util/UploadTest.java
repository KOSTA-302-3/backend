package web.mvc.santa_backend.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import web.mvc.santa_backend.common.S3.S3Uploader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UploadTest {

    @Autowired
    S3Uploader s3Uploader;

    @MockitoBean
    private S3Client s3Client;

    @Test
    public void uploadTest() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "dummy".getBytes());

        PutObjectResponse mockResponse = PutObjectResponse.builder().build();

        // putObject 호출 시 실제 AWS 호출 없이 mockResponse 반환
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(mockResponse);
        // when
        String result = s3Uploader.uploadFile(file, "test");

        // then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("https://"));
        verify(s3Client, times(1))
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
