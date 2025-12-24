package web.mvc.santa_backend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import web.mvc.santa_backend.chat.handler.WebsocketHandler;
import web.mvc.santa_backend.chat.handler.notificationWebSocketHandler;
import web.mvc.santa_backend.chat.interceptor.ChatInterceptor;
import web.mvc.santa_backend.chat.interceptor.NotificationInterceptor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebsocketHandler websocketHandler;
    private final notificationWebSocketHandler notificationWebSocketHandler;
    private final ChatInterceptor chatInterceptor;
    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketHandler,"/ws/chat/**")
                //인터셉터도 붙이기.
                .addInterceptors(chatInterceptor)
                .setAllowedOrigins("*");

        registry.addHandler(notificationWebSocketHandler, "/ws/notification/**")
                .addInterceptors(notificationInterceptor)
                .setAllowedOrigins("*");

    }
}
