package web.mvc.santa_backend.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.admin.dto.AdminDTO;
import web.mvc.santa_backend.admin.service.AdminService;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "AdminController API", description = "관리자 API")
public class AdminController {

    private final AdminService adminService;

    /**
     * 전체 유저 목록 조회
     */
    @Operation(summary = "전체 유저 목록 조회", description = "page 0부터 시작")
    @GetMapping("/users/{page}")
    public ResponseEntity<?> getAllUsers(@PathVariable int page) {
        log.info("getAllUsers/ page: {}", page);
        Page<UserSimpleDTO> users = adminService.getAllUsers(page);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * 유저 상세 조회
     */
    @Operation(summary = "유저 상세 조회")
    @GetMapping("/users/detail/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId) {
        log.info("getUserDetail/ userId: {}", userId);
        UserResponseDTO user = adminService.getUserDetail(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * 유저 정지 (기간별)
     */
    @Operation(summary = "유저 정지 (기간별)", description = "days: 7(7일), 30(30일), 365(1년), -1(영구)")
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<?> suspendUser(
            @PathVariable Long userId,
            @RequestParam int days,
            @RequestParam String category,
            @RequestParam(required = false) String detail) {
        log.info("suspendUser/ userId: {}, days: {}, category: {}", userId, days, category);
        AdminDTO ban = adminService.suspendUser(userId, days, category, detail);
        return ResponseEntity.status(HttpStatus.CREATED).body(ban);
    }

    /**
     * 유저 정지 해제
     */
    @Operation(summary = "유저 정지 해제")
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        log.info("activateUser/ userId: {}", userId);
        adminService.activateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("유저 정지가 해제되었습니다.");
    }

    /**
     * 유저 탈퇴 처리
     */
    @Operation(summary = "유저 탈퇴 처리 (소프트 삭제)")
    @DeleteMapping("/users/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        log.info("deactivateUser/ userId: {}", userId);
        UserResponseDTO user = adminService.deactivateUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * 유저 정지 내역 조회
     */
    @Operation(summary = "유저 정지 내역 조회")
    @GetMapping("/users/{userId}/bans")
    public ResponseEntity<?> getUserBans(@PathVariable Long userId) {
        log.info("getUserBans/ userId: {}", userId);
        List<AdminDTO> bans = adminService.getUserBans(userId);
        return ResponseEntity.status(HttpStatus.OK).body(bans);
    }
}
