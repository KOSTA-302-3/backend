package web.mvc.santa_backend.chat.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import web.mvc.santa_backend.chat.dto.*;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.chat.service.ChatroomService;
import web.mvc.santa_backend.chat.service.MessageService;
import web.mvc.santa_backend.common.enumtype.MessageType;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.ChatroomNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.NotFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatroomManager {
    private final Map<Long, Map<Long, WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatroomMemberRepository chatroomMemberRepository;


    /**
     * 채팅방 입장 혹은 생성 후 입장.
     * @param session
     */
    public void addSession(WebSocketSession session) {
        Long userId = (Long)session.getAttributes().get("userId");
        Long roomId = (Long)session.getAttributes().get("roomId");
        chatRooms.computeIfAbsent(roomId, k -> new HashMap<>()).put(userId, session);
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(roomId, userId).orElseThrow(() -> new NotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));
        ReadUpdateDTO messageDTO = ReadUpdateDTO
                .builder()
                .messageType(MessageType.STATUS)
                .userId(userId)
                .lastRead(chatroomMember.getLastRead())
                .online(true)
                .build();
        broadcast(messageDTO, roomId);
    }

    public void removeSession(Long roomId, Long userId) {
        Map<Long, WebSocketSession> roomSessions = chatRooms.get(roomId);
        if(roomSessions!=null) {
            roomSessions.remove(userId);
        }
        ReadUpdateDTO messageDTO = ReadUpdateDTO
                .builder()
                .messageType(MessageType.STATUS)
                .userId(userId)
                .online(false)
                .build();
        broadcast(messageDTO, roomId);
    }

    public void broadcast(OutboundMessage outboundMessage, Long roomId) {
        objectMapper.registerModule(new JavaTimeModule()); //
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("outboundMessage={}", outboundMessage);
        String message = null;
        try {
            message = objectMapper.writeValueAsString(outboundMessage);
        } catch (JsonProcessingException e) {
            //TODO 무슨 예외를.. 던져야하는가..
            throw new RuntimeException("메시지 이상함");
        }
        Map<Long, WebSocketSession> chatroom = chatRooms.get(roomId);

        if(chatroom==null) {
            return;
        }

        try {
            for(WebSocketSession s : chatroom.values()) {
                s.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            //TODO 무슨 예외를.. 아니.. 예외를 던져야하나..?
            throw new RuntimeException(e);
        }
    }

    public WebSocketSession getSession(Long roomId, Long userId) {
        Map<Long, WebSocketSession> roomSessions = chatRooms.get(roomId);
        return roomSessions != null ? roomSessions.get(userId) : null;
    }

    public Map<Long, WebSocketSession> getRoomSessions(Long roomId) {
        return chatRooms.getOrDefault(roomId, Map.of());
    }

    public int countRoomMembers(Long roomId) {
        Map<Long, WebSocketSession> roomSessions = chatRooms.get(roomId);
        return roomSessions != null ? roomSessions.size() : 0;
    }
}
