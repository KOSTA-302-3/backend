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
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.chat.service.ChatroomService;
import web.mvc.santa_backend.chat.service.MessageService;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.ChatroomNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;

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


    /**
     * 채팅방 입장 혹은 생성 후 입장.
     * @param session
     */
    public void addSession(WebSocketSession session) {
        Long userId = (Long)session.getAttributes().get("userId");
        Long roomId = (Long)session.getAttributes().get("roomId");
        chatRooms.computeIfAbsent(roomId, k -> new HashMap<>()).put(userId, session);
    }

    public void removeSession(Long roomId, Long userId) {
        Map<Long, WebSocketSession> roomSessions = chatRooms.get(roomId);
        if(roomSessions!=null) {
            roomSessions.remove(userId);
        }
    }

    public void broadcast(OutboundChatMessageDTO outboundChatMessageDTO) {
        objectMapper.registerModule(new JavaTimeModule()); //
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Long roomId = outboundChatMessageDTO.getChatroomId();
        String message = null;
        try {
            message = objectMapper.writeValueAsString(outboundChatMessageDTO);
        } catch (JsonProcessingException e) {
            //TODO 무슨 예외를.. 던져야하는가..
            throw new RuntimeException("메시지 이상함");
        }
        Map<Long, WebSocketSession> chatroom = chatRooms.get(roomId);
        if(chatroom==null) {
            throw new ChatroomNotFoundException(ErrorCode.CHATROOM_NOT_FOUND);
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
