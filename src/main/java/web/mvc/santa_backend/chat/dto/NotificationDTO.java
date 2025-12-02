package web.mvc.santa_backend.chat.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.NotificationType;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long notificationId;
    private Long userId;
    private String message;
    private String link;
    private Boolean isRead;
    private NotificationType type;
    private LocalDateTime createdAt;
    private Long actionUserId;
}
