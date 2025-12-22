package web.mvc.santa_backend.chat.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.MessageType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OutboundChatMessageDTO {
    private Long id;
    private String type;
    private Long userId;
    private String username;
    private String avatarUrl;
    private String text;
    private LocalDateTime ts;
    private MessageType messageType;
    private Long unreadCount;
}
