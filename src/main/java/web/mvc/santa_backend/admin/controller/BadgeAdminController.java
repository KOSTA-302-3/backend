package web.mvc.santa_backend.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.common.S3.S3Uploader;
import web.mvc.santa_backend.user.dto.BadgeDTO;
import web.mvc.santa_backend.user.service.BadgeService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/badge")
@Slf4j
@Tag(name = "Badge Admin API", description = "배지 관리 API (관리자용)")
public class BadgeAdminController {

    private final BadgeService badgeService;
    private final S3Uploader s3Uploader;

    @Operation(summary = "배지 등록")
    @PostMapping
    public ResponseEntity<?> addBadge(@RequestBody BadgeDTO badgeDTO) {
        BadgeDTO badge = badgeService.addBadge(badgeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(badge);
    }

    @Operation(summary = "배지 수정")
    @PutMapping("/{badgeId}")
    public ResponseEntity<?> updateBadge(@PathVariable Long badgeId, @RequestBody BadgeDTO badgeDTO) {
        BadgeDTO badge = badgeService.updateBadge(badgeId, badgeDTO);
        return ResponseEntity.status(HttpStatus.OK).body(badge);
    }

    @Operation(summary = "배지 삭제")
    @DeleteMapping("/{badgeId}")
    public ResponseEntity<?> deleteBadge(@PathVariable Long badgeId) {
        badgeService.deleteBadge(badgeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "배지 이미지 업로드")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBadgeImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.");
            }

            // S3에 업로드 (badges 폴더에 저장)
            String imageUrl = s3Uploader.uploadFile(file, "badges");
            
            // URL 반환
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("이미지 업로드 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드 실패: " + e.getMessage());
        }
    }
}
