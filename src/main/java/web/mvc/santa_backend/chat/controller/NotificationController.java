package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.service.NotificationService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "NotificationController API", description = "알림 CRUD용 API")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/api/notification")
    public ResponseEntity<?> getNotification(){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/notification")
    public ResponseEntity<?> createNotification(@RequestBody NotificationDTO notification){
        log.info("테스트중");
        notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
