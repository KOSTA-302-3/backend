package web.mvc.santa_backend.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChatMessage {
    private String type;
    private String roomId;
    private String senderId;
    private String message;
    private LocalDateTime time;
}
