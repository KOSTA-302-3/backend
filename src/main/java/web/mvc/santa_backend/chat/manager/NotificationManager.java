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
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.common.exception.ChatroomNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationManager {
    private final Map<Long, WebSocketSession> notificationSession = new ConcurrentHashMap<>();


    /**
     * 채팅방 입장 혹은 생성 후 입장.
     * @param session
     */
    public void addSession(WebSocketSession session) {
        Long userId = (Long)session.getAttributes().get("userId");
        if (userId == null) {
            // 인증 안 된 소켓 or 인터셉터 누락
            return;
        }
        notificationSession.put(userId, session);
    }

    public void removeSession(WebSocketSession session) {
        notificationSession.entrySet().removeIf(
                entry -> entry.getValue().equals(session)
        );
    }

    /**
     * type은 Notification, Chat
     * @param userId
     * @param type
     */
    public void sendNewNotification(Long userId, String type) {
        WebSocketSession session = notificationSession.get(userId);

        if (session == null || !session.isOpen()) return;

        try {
            session.sendMessage(new TextMessage(type));
        } catch (IOException e) {
            log.warn("알림 전송 실패 userId={}", userId, e);
        }
    }
}
