package web.mvc.santa_backend.user.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserRequestDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserController API", description = "UserController API")
public class UserContoller {

    private final UserService userService;

    /* security 로그인 테스트용 */
    /*@GetMapping("/")
    public String test() {
        log.info("test called...");

        return "index";
    }

    @GetMapping("/fail")
    public String fail() {
        log.info("fail called...");

        return "fail";
    }*/

    /* 회원가입 */
    @Operation(summary = "아이디(username) 중복체크")
    @GetMapping("/api/users/username/{username}")
    public String checkUsernameDuplication(@PathVariable String username) {
        log.info("checkUsernameDuplication/ username: {}", username);
        return userService.checkUsernameDuplication(username);
    }

    @Operation(summary = "이메일 중복체크")
    @GetMapping("/api/users/email/{email}")
    public String checkEmailDuplication(@PathVariable String email) {
        log.info("checkEmailDuplication/ email: {}", email);
        return userService.checkEmailDuplication(email);
    }

    //@Operation(summary = "전화번호 중복체크 (보류)")

    @Operation(summary = "회원가입")
    @PostMapping("/api/users")
    public String register(@RequestBody UserRequestDTO userDTO) {
        log.info("register/ user: {}", userDTO);
        userService.register(userDTO);

        return "회원가입이 완료되었습니다.";
    }

    /* 유저 조회, 수정, 탈퇴 */
    @Operation(summary = "아이디(username)로 유저 목록 조회(검색)", description = "page 0부터 시작")
    @GetMapping("/api/users/{username}/{page}")
    public ResponseEntity<?> getUsersByUsername(@PathVariable String username, @PathVariable int page) {
        log.info("username: {}, page: {}", username, page);
        Page<UserSimpleDTO> users = userService.getUsersByUsername(username, page);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "userId로 개인 유저 조회")
    @GetMapping("/api/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("getUserById/ id: {}", id);
        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "유저 정보 수정 (TODO: password)"
            , description = "수정 항목: username, profileImage, description, level, isPrivate" +
            "Swagger에서 테스트 시, Request body에서 private을 isPrivate 으로 바꿔줘야 함")
    @PutMapping("/api/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        log.info("updateUser/ user: {}", userDTO);
        UserResponseDTO updateUser = userService.updateUsers(id, userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updateUser);
    }

    @Operation(summary = "유저 탈퇴")
    @PutMapping("/api/users/softdelete/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        log.info("deactivateUser/ id: {}", id);
        UserResponseDTO deleteUser = userService.deactivateUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(deleteUser);
    }

    @Operation(summary = "유저 삭제")
    @DeleteMapping("/api/users/harddelete/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("deleteUser/ id: {}", id);
        userService.deleteUser(id);

        return "유저 삭제 완료";
    }
}
