package web.mvc.santa_backend.chat.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;
import web.mvc.santa_backend.chat.dto.ChatMessage;
import web.mvc.santa_backend.chat.service.MessagePublisher;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {

    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // roomId → Set<Session>
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // sessionId → Session (전역 세션 저장)
    private final ConcurrentMap<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();


    /**
     * WebSocket 연결 시 전역 sessions에 등록
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        if (session != null && session.isOpen()) {
            sessionsById.put(session.getId(), session);
        }

        System.out.println("WebSocket connected - URI: " + session.getUri());
        System.out.println("WebSocket sessionId: " + session.getId());
    }


    /**
     * 메시지 수신 처리
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        ChatMessage msg = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        // --- 방 입장 ---
        if ("ENTER".equals(msg.getType())) {

            roomSessions
                    .computeIfAbsent(msg.getRoomId(), key -> ConcurrentHashMap.newKeySet())
                    .add(session);

            return;
        }

        // --- 일반 메시지는 RabbitMQ로 발행 ---
        messagePublisher.publish(msg);
    }


    /**
     * WebSocket 연결 종료 시 정리
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        if (session == null) return;

        // 전역 세션맵에서 제거
        sessionsById.remove(session.getId());

        // 모든 방에서 제거
        removeSessionFromAllRooms(session);

        System.out.println("WebSocket closed - sessionId: " + session.getId() + ", status: " + status);
    }


    /**
     * 모든 방에서 세션 제거
     */
    private void removeSessionFromAllRooms(WebSocketSession session) {

        for (Map.Entry<String, Set<WebSocketSession>> entry : roomSessions.entrySet()) {

            Set<WebSocketSession> set = entry.getValue();
            if (set == null) continue;

            set.remove(session);

            // 방에 아무도 없으면 방 삭제
            if (set.isEmpty()) {
                roomSessions.remove(entry.getKey());
            }
        }
    }
}
