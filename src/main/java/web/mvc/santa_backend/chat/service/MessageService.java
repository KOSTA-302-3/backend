package web.mvc.santa_backend.chat.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.ReadUpdateDTO;

public interface MessageService {
    /**
     * 채팅방id로 그 채팅방 내의 메시지를 일정개수(300개 예상) 가지고 오는 메서드
     * @param chatroomId 메시지를 가지고 올 채팅방의 id
     * @param userId 인증용
     * @param page 0 ~ 스크롤을 올리면 페이지가 늘어나게..
     * @return
     */
    Page<OutboundChatMessageDTO> getOutboundChatMessages(Long chatroomId, Long userId, int page);

    /**
     * 메시지를 DB에 저장하는 메서드
     * @param inboundChatMessageDTO
     */
    OutboundChatMessageDTO createMessage(InboundChatMessageDTO inboundChatMessageDTO);

    /**
     * 기존 참여자가 입장 시 unreadCount 숫자를 업데이트 할 DTO를 만드는 메서드
     * @return
     */
    ReadUpdateDTO updateFrontUnreadCount(Long chatroomId, Long userId);

    Long countAllUnreadMessages(Long userId);
}
