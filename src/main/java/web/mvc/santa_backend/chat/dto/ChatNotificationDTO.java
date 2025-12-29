package web.mvc.santa_backend.chat.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatNotificationDTO {
    private Long chatroomId;
    private String type;
}
