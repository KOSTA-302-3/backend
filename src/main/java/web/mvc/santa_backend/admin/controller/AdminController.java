package web.mvc.santa_backend.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.admin.dto.AdminReportDTO;
import web.mvc.santa_backend.admin.dto.AdminUserDTO;
import web.mvc.santa_backend.admin.dto.BansDTO;
import web.mvc.santa_backend.admin.dto.DashboardStatsDTO;
import web.mvc.santa_backend.admin.dto.SuspendRequestDTO;
import web.mvc.santa_backend.admin.service.AdminService;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "AdminController API", description = "관리자 API")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    /**
     * 대시보드 통계 조회
     */
    @Operation(summary = "대시보드 통계 조회", description = "총 가입자 수, 오늘 가입자 수, 오늘 게시글 수")
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        log.info("getDashboardStats");
        DashboardStatsDTO stats = adminService.getDashboardStats();
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }

    /**
     * 전체 유저 목록 조회 (기존 로직 - UserSimpleDTO)
     */
    @Operation(summary = "전체 유저 목록 조회", description = "page 0부터 시작")
    @GetMapping("/users/{page}")
    public ResponseEntity<?> getAllUsers(@PathVariable int page) {
        log.info("getAllUsers/ page: {}", page);
        Page<UserSimpleDTO> users = adminService.getAllUsers(page);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * 상태별 유저 목록 조회 (드롭다운 필터용)
     */
    @Operation(summary = "상태별 유저 목록 조회", description = "status: ALL, ACTIVE, BANNED")
    @GetMapping("/users/filter/{status}/{page}")
    public ResponseEntity<?> getAdminUsersByStatus(@PathVariable String status, @PathVariable int page) {
        log.info("getAdminUsersByStatus/ status: {}, page: {}", status, page);
        Page<AdminUserDTO> users = adminService.getAdminUsersByStatus(page, status);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * 유저 상세 조회
     */
    @Operation(summary = "유저 상세 조회")
    @GetMapping("/users/detail/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId) {
        log.info("getUserDetail/ userId: {}", userId);
        UserResponseDTO user = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * 유저 정지 (기간별)
     */
    @Operation(summary = "유저 정지 (기간별)", description = "days: 7(7일), 30(30일), 365(1년), -1(영구)")
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<?> suspendUser(
            @PathVariable Long userId,
            @RequestBody SuspendRequestDTO requestDTO) {
        log.info("suspendUser/ userId: {}, days: {}, category: {}", userId, requestDTO.getDays(), requestDTO.getCategory());
        BansDTO ban = adminService.suspendUser(userId, requestDTO.getDays(), requestDTO.getCategory(), requestDTO.getDetail());
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
        List<BansDTO> bans = adminService.getUserBans(userId);
        return ResponseEntity.status(HttpStatus.OK).body(bans);
    }

    /**
     * 관리자용 신고 목록 조회 (페이징) - 정지 이력 포함
     */
    @Operation(summary = "관리자용 신고 목록 조회", description = "page 0부터 시작, type: USER, POST, REPLY, 정지 이력 포함")
    @GetMapping("/reports/{type}/{page}")
    public ResponseEntity<?> getAdminReports(@PathVariable ReportType type, @PathVariable int page) {
        log.info("getAdminReports/ type: {}, page: {}", type, page);
        try {
            Page<AdminReportDTO> reports = adminService.getAdminReports(type, page);
            log.info("관리자용 신고 목록 조회 성공: {} 건", reports.getTotalElements());
            return ResponseEntity.status(HttpStatus.OK).body(reports);
        } catch (Exception e) {
            log.error("관리자용 신고 목록 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("신고 목록 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 신고 승인 (유저 정지 + 신고 삭제)
     */
    @Operation(summary = "신고 승인 (자동 정지)", description = "days: 7(7일), 30(30일), 365(1년), -1(영구)")
    @PostMapping("/reports/{reportId}/approve")
    public ResponseEntity<?> approveReport(
            @PathVariable Long reportId,
            @RequestParam(defaultValue = "7") int days) {
        log.info("approveReport/ reportId: {}, days: {}", reportId, days);
        adminService.approveReport(reportId, days);
        return ResponseEntity.status(HttpStatus.OK).body("신고가 승인되고 유저가 정지되었습니다.");
    }

    /**
     * 신고 거절 (신고만 삭제)
     */
    @Operation(summary = "신고 거절 (신고만 삭제)")
    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long reportId) {
        log.info("deleteReport/ reportId: {}", reportId);
        adminService.deleteReport(reportId);
        return ResponseEntity.status(HttpStatus.OK).body("신고가 거절되었습니다.");
    }
    
    /**
     * 전체 게시물 목록 조회 (페이징)
     */
    @Operation(summary = "전체 게시물 목록 조회", description = "page 0부터 시작, 페이지당 20개")
    @GetMapping("/posts/{page}")
    public ResponseEntity<?> getAllPosts(@PathVariable int page) {
        log.info("getAllPosts/ page: {}", page);
        Page<web.mvc.santa_backend.post.dto.PostDTO> posts = adminService.getAllPosts(page);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
    
    /**
     * 게시물 삭제
     */
    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        log.info("deletePost/ postId: {}", postId);
        adminService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 삭제되었습니다.");
    }
}
