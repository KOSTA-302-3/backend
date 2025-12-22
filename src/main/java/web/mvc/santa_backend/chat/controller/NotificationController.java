package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.service.NotificationService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "NotificationController API", description = "알림 CRUD용 API")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/api/notification/{userId}/{page}")
    public ResponseEntity<?> getNotification(@PathVariable Long userId, @PathVariable int page){
        Page<NotificationDTO> notifications = notificationService.getNotificationByUserId(userId, page);
        return ResponseEntity.status(HttpStatus.OK).body(notifications);
    }

    @PostMapping("/api/notification")
    public ResponseEntity<?> createNotification(@RequestBody NotificationDTO notification){
        notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/notification/{notificationId}")
    public ResponseEntity<?> checkNotification(@PathVariable Long notificationId){
        notificationService.deleteNotificationById(notificationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
