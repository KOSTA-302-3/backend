package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.service.NotificationService;
import web.mvc.santa_backend.common.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "NotificationController API", description = "알림 CRUD용 API")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 미구현
     * @param page
     * @param customUserDetails
     * @return
     */
    @GetMapping("/api/notificationAll/{page}")
    public ResponseEntity<?> getAllNotifications(@PathVariable Integer page, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        notificationService.getAllNotificationByUserId(userId, page);
        return null;
    }

    @GetMapping("/api/notification/{page}")
    public ResponseEntity<?> getNotification(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable int page){
        Long userId = customUserDetails.getUser().getUserId();
        Page<NotificationDTO> notifications = notificationService.getNotificationByUserId(userId, page);
        return ResponseEntity.status(HttpStatus.OK).body(notifications);
    }

    @PostMapping("/api/notification")
    public ResponseEntity<?> createNotification(@RequestBody NotificationDTO notification){
        notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/notification")
    public ResponseEntity<?> checkAllNotification(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long Id = customUserDetails.getUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/api/notification/{notificationId}")
    public ResponseEntity<?> checkNotification(@PathVariable Long notificationId){
        notificationService.deleteNotificationById(notificationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
