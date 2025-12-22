package web.mvc.santa_backend.chat.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.MessageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReadUpdateDTO {
    private Long chatroomId;
    private Long userId; // 기존에 참여하던 사람. 읽지 않고 있다가, 채팅방에 다시 들어와서 글을 읽은 사람
    private MessageType messageType;
    private Long lastReadFrom; // userId의 유저가 이 채팅방에서 마지막으로 읽은 MessageId
    private Long lastReadTo; // 지금 현재 채팅방의 가장 최신 MessageId
    //기존 유저가 재입장이라면.. 브로드캐스트를 통해 이 DTO를 보내서.. 프론트에서 lastReadFrom+1 ~ lastReadTo의 unreadCount를 -1
}
