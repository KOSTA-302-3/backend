package web.mvc.santa_backend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class RedisConnectionTester implements CommandLineRunner {

    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=======================================================");
        System.out.println("🚀 [Redis 진단 시작] 설정값 및 연결 상태를 점검합니다.");
        System.out.println("=======================================================");

        // 1. 환경변수 로딩 확인
        System.out.println("[1] 설정값 확인");
        System.out.println("   - Host: " + host);
        System.out.println("   - Port: " + port);
        // 비밀번호는 보안상 길이와 앞 2글자만 출력
        String maskedPwd = (password != null && password.length() > 2) 
                ? password.substring(0, 2) + "**** (길이: " + password.length() + ")" 
                : "NULL";
        System.out.println("   - Password: " + maskedPwd);

        // 2. 실제 연결 테스트 (PING)
        System.out.println("\n[2] 연결 테스트 (PING 시도...)");
        try {
            String response = redisConnectionFactory.getConnection().ping();
            System.out.println("   ✅ 연결 성공! 응답: " + response);
            System.out.println("   (Redis와 정상적으로 통신하고 있습니다.)");
        } catch (Exception e) {
            System.err.println("   ❌ 연결 실패! 에러가 발생했습니다.");
            System.err.println("   💥 에러 메시지: " + e.getMessage());
            
            // 가장 중요한 '진짜 원인' 찾기
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("   🧐 진짜 원인 (Caused by): " + cause.getMessage());
                if (cause.getCause() != null) {
                    System.err.println("   🧐 더 깊은 원인: " + cause.getCause().getMessage());
                }
            }
            
            System.err.println("\n--- [상세 스택 트레이스] ---");
            e.printStackTrace();
        }
        System.out.println("=======================================================\n");
    }
}