package web.mvc.santa_backend.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import web.mvc.santa_backend.chat.manager.NotificationManager;

@Component
@Slf4j
@RequiredArgsConstructor
public class notificationWebSocketHandler extends TextWebSocketHandler {
    private final NotificationManager notificationManager;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("알림 웹소켓 접속됨");
        Long userId = (Long) session.getAttributes().get("userId");
        System.out.println("userId : "+userId);
        notificationManager.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("afterConnectionClosed");
        notificationManager.removeSession(session);
    }
}
