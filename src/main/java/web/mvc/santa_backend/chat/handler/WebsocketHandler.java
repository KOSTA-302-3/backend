package web.mvc.santa_backend.chat.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.manager.ChatroomManager;
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.chat.service.ChatroomService;
import web.mvc.santa_backend.chat.service.MessageService;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatroomMemberService chatroomMemberService;
    private final MessageService messageService;
    private final ChatroomManager chatroomManager;
    /**
     * 접속시
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        Long userId = (Long) session.getAttributes().get("userId");
        ChatroomMemberDTO chatroomMemberDTO = ChatroomMemberDTO.builder()
                .chatroomId(roomId)
                .userId(userId)
                .build();
        chatroomMemberService.enterChatroom(chatroomMemberDTO, session);
    }

    /**
     * 메시지가 왔을때
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        Long userId = (Long) session.getAttributes().get("userId");
        //TextMessage객체에서 Json 타입 메시지 가지고 옴
        String payload = message.getPayload();
        //Json타입 메시지를 DTO로 변환
        InboundChatMessageDTO messageDTO = objectMapper.readValue(payload, InboundChatMessageDTO.class);
        //메시지에는 메시지내용, 타입만 들어있음(선택적으로 부모메시지Id도 같이) roomId, userId 세팅해줌.
        messageDTO.setChatroomId(roomId);
        messageDTO.setUserId(userId);
        
        //메시지를 DB에 저장하면서 저장된 메시지를 다시 out객체로 가지고 옴
        //TODO 이 과정에서 방에 실제 접속중인 사람의 lastRead를 update
        OutboundChatMessageDTO outMessageDTO = messageService.createMessage(messageDTO);

        //메시지를 채팅방에 있는 모든 사람들에게 broadcast.
        chatroomManager.broadcast(outMessageDTO);
    }

    /**
     * 접속이 끊어졌을 때(채팅방을 나갔을때)
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("afterConnectionClosed");

    }
}
