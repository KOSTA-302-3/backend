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
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime time;
    private boolean isUnread;
}
