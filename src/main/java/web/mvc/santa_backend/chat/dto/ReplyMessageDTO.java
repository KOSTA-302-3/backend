package web.mvc.santa_backend.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReplyMessageDTO {
    private Long messageId;
    private Long userId;
    private String payload;
}
