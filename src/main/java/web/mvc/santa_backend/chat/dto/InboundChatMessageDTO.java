package web.mvc.santa_backend.chat.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.MessageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class InboundChatMessageDTO {
    private Long userId; //jwt에서 꺼낼 예정
    private Long chatroomId; //인터셉터에서 session에 담아서 핸들러로 보내고, 핸들러에서 DTO에 넣을 예정..
    private Long replyMessageId; // 답글시 부모 메시지id
    private String payload; // 메시지 내용
    private MessageType type; // 메시지 타입(TEXT/IMAGE)
}
