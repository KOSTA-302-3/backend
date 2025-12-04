package web.mvc.santa_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.common.S3.S3Uploader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UploadTestController {
    private final S3Uploader s3Uploader;

    @PostMapping("/api/uploadfile")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Uploading file {}", file.getOriginalFilename());
        String url = s3Uploader.uploadFile(file, "test");
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @PostMapping("/api/uploadfiles")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        log.info("Uploading files {}", files);

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            urls.add(s3Uploader.uploadFile(file, "test"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(urls);
    }
}
