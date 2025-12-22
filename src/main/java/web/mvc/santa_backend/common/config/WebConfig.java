package web.mvc.santa_backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:5173", "http://192.168.0.19:5173", "ws://192.168.0.19:5173", "https://santa-sns.o-r.kr/", "http://santa-sns.o-r.kr/") // React 주소 허용 (또는 "*"로 모든 주소 허용)
                .allowedMethods("*") // GET, POST 등 모든 메서드 허용
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키/인증 정보 포함 허용
    }
}