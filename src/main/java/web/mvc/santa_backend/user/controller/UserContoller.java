package web.mvc.santa_backend.user.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.common.S3.S3Uploader;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.common.enumtype.CustomItemType;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.UnauthorizedException;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.CustomDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserRequestDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.service.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "UserController API", description = "UserController API")
public class UserContoller {

    private final UserService userService;
    private final CustomService customService;
    private final FollowService followService;
    private final BlockService blockService;
    private final ReportService reportService;
    private final BadgeService badgeService;
    private final ColorService colorService;
    private final S3Uploader s3Uploader;

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
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserResponseDTO user = null;

        if (customUserDetails == null) {
            user = userService.getUserById(id, false);
        } else if (customUserDetails.getUser().getUserId().equals(id)) {
            user = userService.getUserById(id, true);
        } else {
            user = userService.getUserById(id, false);
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "로그인 된 유저 조회 (유저 페이지)",
            description = "현재 로그인 된 유저 조회")
    @GetMapping("/me")
    public ResponseEntity<?> getLoginUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long loginUserId = customUserDetails.getUser().getUserId();
        log.info("getLoginUser/ loginId: {}", loginUserId);
        UserResponseDTO user = userService.getUserById(loginUserId, true);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /* 유저 정보 수정 */
    @Operation(summary = "유저 정보 수정 (TODO: password)"
            , description = "수정 항목: username, profileImage, description, level" +
            "Swagger에서 테스트 시, Request body에서 private을 isPrivate 으로 바꿔줘야 함")
    @PutMapping
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserRequestDTO userDTO) {
        log.info("updateUser/ user: {}", userDTO);
        UserResponseDTO updateUser = userService.updateUsers(customUserDetails.getUser().getUserId(), userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updateUser);
    }

    @Operation(summary = "공개/비공개 변경", description = "toPrivate: true 시 비공개로 전환 / false 시 공개로 전환")
    @PutMapping("/privacy")
    public ResponseEntity<?> updatePrivate(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserResponseDTO updateUser = userService.updatePrivacy(customUserDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(updateUser);
    }

    /* 프로필 이미지 업로드 */
    @PostMapping("/upload/profile")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Uploading file {}", file.getOriginalFilename());
        String url = s3Uploader.uploadFile(file, "user");
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    /* 유저 탈퇴(상태 수정/삭제) */
    @Operation(summary = "유저 탈퇴")
    @DeleteMapping("/softdelete")
    public ResponseEntity<?> deactivateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("deactivateUser/ id: {}", customUserDetails.getUser().getUserId());
        UserResponseDTO deleteUser = userService.deactivateUser(customUserDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(deleteUser);
    }

    @Operation(summary = "유저 탈퇴 복구")
    @PutMapping("/recover")
    public ResponseEntity<?> reactivateUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("reactivateUser/ id: {}", customUserDetails.getUser().getUserId());
        UserResponseDTO recoverUser = userService.reactivateUser(customUserDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(recoverUser);
    }

    @Operation(summary = "유저 삭제")
    @DeleteMapping("/harddelete/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("deleteUser/ id: {}", id);
        userService.deleteUser(id);

        return "유저 삭제 완료";
    }

    /* Custom 프로필 꾸미기 */
    @Operation(summary = "커스텀(배지/색상) 보유 목록 조회")
    @GetMapping("/custom/{type}/{page}")
    public ResponseEntity<?> getCustoms(@PathVariable CustomItemType type, @PathVariable int page, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();

        Page<?> customs = switch (type) {
            case BADGE -> badgeService.getBadgesByUserId(userId, page);
            case COLOR -> colorService.getColorsByUserId(userId, page);
        };

        return ResponseEntity.status(HttpStatus.OK).body(customs);
    }

    @Operation(summary = "프로필 커스텀(배지/색상) 바꾸기")
    @PutMapping("/custom/{type}/{id}")
    public ResponseEntity<?> changeCustom(@PathVariable CustomItemType type, @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        CustomDTO customDTO = customService.updateCustom(type, userId, id);

        return ResponseEntity.status(HttpStatus.OK).body(customDTO);
    }


    /* 팔로우 관련 조회 */
    @Operation(summary = "팔로워 조회 (전체 리스트)")
    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long id) {
        List<UserSimpleDTO> followers = followService.getFollowers(id);

        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }

    @Operation(summary = "팔로잉 조회 (전체 리스트)")
    @GetMapping("/{id}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable Long id) {
        List<UserSimpleDTO> followings = followService.getFollowings(id);

        return ResponseEntity.status(HttpStatus.OK).body(followings);
    }

    @Operation(summary = "팔로워 조회 (페이징)")
    @GetMapping("/{id}/followers/{page}")
    public ResponseEntity<?> getFollowers(@PathVariable Long id, @PathVariable int page) {
        Page<UserSimpleDTO> followers = followService.getFollowers(id, page);

        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }

    @Operation(summary = "팔로잉 조회 (페이징)")
    @GetMapping("/{id}/followings/{page}")
    public ResponseEntity<?> getFollowings(@PathVariable Long id, @PathVariable int page) {
        Page<UserSimpleDTO> followings = followService.getFollowings(id, page);

        return ResponseEntity.status(HttpStatus.OK).body(followings);
    }

    @Operation(summary = "대기 중인 팔로워 조회 (페이징)", description = "현재 유저가 대기 팔로워 수락/거절 선택을 위함")
    @GetMapping("/pending/{page}")
    public ResponseEntity<?> getPendingFollowers(@PathVariable int page, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Page<UserSimpleDTO> pendingFollowers = followService.getPendingFollowers(customUserDetails.getUser().getUserId(), page);

        return ResponseEntity.status(HttpStatus.OK).body(pendingFollowers);
    }

    /* followCount 동기화 (관리자에서 처리?) */
    @Operation(summary = "followingCount, followerCount 동기화 (관리자용?)",
            description = "Follows 의 레코드 수로부터 Users_followingCount, followerCount 맞추기" +
                    "return: 팔로우 수가 맞지 않았던 유저들 반환")
    @GetMapping("/sync")
    public ResponseEntity<?> sync() {
        List<UserResponseDTO> syncUsers = followService.updateFollowCounts();

        return ResponseEntity.status(HttpStatus.OK).body(syncUsers);
    }

    /* 차단 조회 */
    @Operation(summary = "차단 목록 조회 (페이징)", description = "로그인한 유저의 차단 목록을 보는 것")
    @GetMapping("/block/{type}/{page}")
    public ResponseEntity<?> getBlocks(@PathVariable BlockType type, @PathVariable int page, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Page<Object> blocks = blockService.getBlocks(customUserDetails.getUser().getUserId(), type, page);

        return ResponseEntity.status(HttpStatus.OK).body(blocks);
    }

    /* 신고 조회 */
    @Operation(summary = "신고 목록 조회 (페이징)", description = "로그인한 유저의 신고 목록을 보는 것")
    @GetMapping("/report/{type}/{page}")
    public ResponseEntity<?> getReports(@PathVariable ReportType type, @PathVariable int page, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Page<Object> reports = reportService.getReportsByUserId(customUserDetails.getUser().getUserId(), type, page);

        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }

    @Operation(summary = "userId 전송 메서드", description = "현재 접속중인 userId를 프론트로 보내는 메서드")
    @GetMapping("/userId")
    public ResponseEntity<?> getUserId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("현재 접속중인 ID : {}",  customUserDetails.getUser().getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(customUserDetails.getUser().getUserId());
    }

}
