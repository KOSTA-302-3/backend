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
    private Long messageId;
    private Long userId;
    private Long chatroomId;
    private ReplyMessageDTO replyMessage;
    private String payload;
    private LocalDateTime createdAt;
    private MessageType type;
    private Long unreadCount;
    //유저id로는 user를 특정하기 어려운 관계로..
    private String username;
}
