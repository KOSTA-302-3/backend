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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "UserController API", description = "UserController API")
public class UserContoller {

    private final UserService userService;

    /* security 로그인 테스트용 */
    @GetMapping("/test")
    public String test() {
        log.info("test called...");

        return "test 입니다.";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    /* 회원가입 */
    @Operation(summary = "아이디(username) 중복체크")
    @GetMapping("/username/{username}")
    public String checkUsernameDuplication(@PathVariable String username) {
        log.info("checkUsernameDuplication/ username: {}", username);
        return userService.checkUsernameDuplication(username);
    }

    @Operation(summary = "이메일 중복체크")
    @GetMapping("/email/{email}")
    public String checkEmailDuplication(@PathVariable String email) {
        log.info("checkEmailDuplication/ email: {}", email);
        return userService.checkEmailDuplication(email);
    }

    //@Operation(summary = "전화번호 중복체크 (보류)")

    @Operation(summary = "회원가입")
    @PostMapping
    public String register(@RequestBody UserRequestDTO userDTO) {
        log.info("register/ user: {}", userDTO);
        userService.register(userDTO);

        return "회원가입이 완료되었습니다.";
    }

    /* 유저 조회 */
    @Operation(summary = "아이디(username)로 유저 목록 조회(검색)", description = "page 0부터 시작")
    @GetMapping("/{username}/{page}")
    public ResponseEntity<?> getUsersByUsername(@PathVariable String username, @PathVariable int page) {
        log.info("username: {}, page: {}", username, page);
        Page<UserSimpleDTO> users = userService.getUsersByUsername(username, page);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "userId로 개인 유저 조회",
            description = "현재 로그인 된 유저 외에도 다른 유저들을 조회하는 거라서 id 받아오기")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("getUserById/ id: {}", id);
        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /* 유저 정보 수정 */
    @Operation(summary = "유저 정보 수정 (TODO: password)"
            , description = "수정 항목: username, profileImage, description, level" +
            "Swagger에서 테스트 시, Request body에서 private을 isPrivate 으로 바꿔줘야 함")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) {
        log.info("updateUser/ user: {}", userDTO);
        UserResponseDTO updateUser = userService.updateUsers(id, userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updateUser);
    }

    @Operation(summary = "공개/비공개 변경", description = "toPrivate: true 시 비공개로 전환 / false 시 공개로 전환")
    @PutMapping("/{id}/private")
    public ResponseEntity<?> updatePrivate(@PathVariable Long id, @RequestParam boolean toPrivate) {
        UserResponseDTO updateUser = userService.updatePrivate(id, toPrivate);

        return ResponseEntity.status(HttpStatus.OK).body(updateUser);
    }

    /* 유저 탈퇴(상태 수정/삭제) */
    @Operation(summary = "유저 탈퇴")
    @PutMapping("/softdelete/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        log.info("deactivateUser/ id: {}", id);
        UserResponseDTO deleteUser = userService.deactivateUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(deleteUser);
    }

    @Operation(summary = "유저 탈퇴 복구")
    @PutMapping("/recover/{id}")
    public ResponseEntity<?> reactivateUser(@PathVariable Long id) {
        log.info("reactivateUser/ id: {}", id);
        UserResponseDTO recoverUser = userService.reactivateUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(recoverUser);
    }

    @Operation(summary = "유저 삭제")
    @DeleteMapping("/harddelete/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("deleteUser/ id: {}", id);
        userService.deleteUser(id);

        return "유저 삭제 완료";
    }

    /* 팔로우 관련 조회 */
    @Operation(summary = "팔로워 조회 (전체 리스트)")
    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long id) {
        List<UserSimpleDTO> followers = userService.getFollowers(id);

        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }

    @Operation(summary = "팔로잉 조회 (전체 리스트)")
    @GetMapping("/{id}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable Long id) {
        List<UserSimpleDTO> followings = userService.getFollowings(id);

        return ResponseEntity.status(HttpStatus.OK).body(followings);
    }

    @Operation(summary = "팔로워 조회 (페이징)")
    @GetMapping("/{id}/followers/{page}")
    public ResponseEntity<?> getFollowers(@PathVariable Long id, @PathVariable int page) {
        Page<UserSimpleDTO> followers = userService.getFollowers(id, page);

        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }

    @Operation(summary = "팔로잉 조회 (페이징)")
    @GetMapping("/{id}/followings/{page}")
    public ResponseEntity<?> getFollowings(@PathVariable Long id, @PathVariable int page) {
        Page<UserSimpleDTO> followings = userService.getFollowings(id, page);

        return ResponseEntity.status(HttpStatus.OK).body(followings);
    }

    @Operation(summary = "대기 중인 팔로워 조회 (페이징)", description = "현재 유저가 대기 팔로워 수락/거절 선택을 위함")
    @GetMapping("/{id}/pending/{page}")
    public ResponseEntity<?> getPendingFollowers(@PathVariable Long id, @PathVariable int page) {
        Page<UserSimpleDTO> pendings = userService.getPendingFollowers(id, page);

        return ResponseEntity.status(HttpStatus.OK).body(pendings);
    }

    @Operation(summary = "followingCount, followerCount 동기화 (관리자용?)",
            description = "Follows 의 레코드 수로부터 Users_followingCount, followerCount 맞추기" +
                    "return: 팔로우 수가 맞지 않았던 유저들 반환")
    @GetMapping("/sync")
    public ResponseEntity<?> sync() {
        List<UserResponseDTO> syncUsers = userService.updateFollowCounts();

        return ResponseEntity.status(HttpStatus.OK).body(syncUsers);
    }
}
