package web.mvc.santa_backend.chat.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import web.mvc.santa_backend.common.security.JWTUtil;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationInterceptor implements HandshakeInterceptor {
    private final JWTUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("beforeHandshake");

        //쿠키에서 jwt 꺼내기
        String token = null;

        List<String> cookies = request.getHeaders().get("Cookie");
        if(cookies == null || cookies.isEmpty()) {//쿠키가 아예 없으면?
            return false;
        }

        for(String cookieHeader : cookies) {
            String[] cookiePairs = cookieHeader.split(";");

            for(String cookie : cookiePairs) {
                String trimmed = cookie.trim();

                if(trimmed.startsWith("Authorization=")) {
                    token = trimmed.substring("Authorization=".length());
                }
            }
        }

        Long userId = jwtUtil.getUserId(token);
        if(userId == null) {
            return false;
        }

        log.info("userId: {}", userId);

        attributes.put("userId", userId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
