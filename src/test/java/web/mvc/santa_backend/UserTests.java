package web.mvc.santa_backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.user.entity.Customs;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.CustomRepository;
import web.mvc.santa_backend.user.repository.UserRepository;
import web.mvc.santa_backend.user.service.UserService;

import java.time.LocalDateTime;

@SpringBootTest
public class UserTests {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomRepository customRepository;


    @Test
    @DisplayName("유저 등록")
    @Transactional
    @Rollback(false)
    void insertUsers() {
        String encPwd = passwordEncoder.encode("1234"); // 비밀번호 암호화

        Users user1 = userRepository.save(
                Users.builder()
                        .username("admin")
                        .password(encPwd)
                        .email("admin@santa.com")
                        .phone("010-1111-1111")
                        .point(0L)
                        .role(UserRole.ADMIN)
                        .state(true)
                        .level(10)
                        .description("관리자 계정입니다.")
                        .followerCount(0L)
                        .followingCount(0L)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        customRepository.save(Customs.builder().user(user1).build());

        Users user2 = userRepository.save(
                Users.builder()
                        .username("mj")
                        .password(encPwd)
                        .email("mj@santa.com")
                        .phone("010-2222-2222")
                        .point(0L)
                        .role(UserRole.USER)
                        .state(true)
                        .level(10)
                        .description("mj 입니다.")
                        .followerCount(0L)
                        .followingCount(0L)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        customRepository.save(Customs.builder().user(user2).build());

        Users user3 = userRepository.save(
                Users.builder()
                        .username("dh")
                        .password(encPwd)
                        .email("dh@santa.com")
                        .phone("010-3333-3333")
                        .point(0L)
                        .role(UserRole.USER)
                        .state(true)
                        .level(10)
                        .description("dh 입니다.")
                        .followerCount(0L)
                        .followingCount(0L)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        customRepository.save(Customs.builder().user(user3).build());
    }

    @Test
    @DisplayName("username으로 유저 목록 조회")
    @Transactional(readOnly = true)
    void selectUsersByUsername() {
        String keyword = "m";

        Pageable pageable = PageRequest.of(1, 10);
        Page<Users> users = userRepository.findByUsernameContainingIgnoreCase(keyword, pageable);

        System.out.println("users: " + users);
    }

    @Test
    @DisplayName("username으로 유저 목록 조회 (JPQL)")
    @Transactional(readOnly = true)
    void selectUsersByUsername2() {
        String keyword = "m";

        Pageable pageable = PageRequest.of(1, 10);
        Page<Users> users = userRepository.findWithCustomByUsername(keyword, pageable);

        System.out.println("users: " + users);
    }

    @Test
    @DisplayName("유저 정보 수정")
    void updateUser() {
        Users user = userRepository.findById(3L)
                //.orElseThrow(()->new DMLException(ErrorCode.));
                .orElseThrow(()->new RuntimeException("수정 실패"));
        user.setUsername("test");
        user.setDescription("test입니다.");
        //user.setLevel(10);
        user.setPrivate(false);

        System.out.println("isPrivate: " + user.isPrivate());
    }

    @Test
    @DisplayName("기본 배지(없음) 등록")
    void insertDefaultBadge() {

    }

    @Test
    @DisplayName("기본 글자색(검정) 등록")
    void insertDefaultColor() {

    }
}
