package web.mvc.santa_backend.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.security.JWTUtil;
import web.mvc.santa_backend.user.entity.Users;

@SpringBootTest
public class JwtTests {

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void createTokenTest() {
        Users testUser = Users.builder()
                .username("test")
                .build();
        String token = jwtUtil.createJwt(testUser, UserRole.USER.toString(), 1000L*60L*30L);   // 30분
    }
}
