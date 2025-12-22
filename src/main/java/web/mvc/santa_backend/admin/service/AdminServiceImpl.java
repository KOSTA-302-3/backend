package web.mvc.santa_backend.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.admin.dto.AdminReportDTO;
import web.mvc.santa_backend.admin.dto.AdminUserDTO;
import web.mvc.santa_backend.admin.dto.BansDTO;
import web.mvc.santa_backend.admin.dto.DashboardStatsDTO;
import web.mvc.santa_backend.admin.entity.Bans;
import web.mvc.santa_backend.admin.repository.BansRepository;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Reports;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.ReportRepository;
import web.mvc.santa_backend.user.repository.UserRepository;
import web.mvc.santa_backend.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BansRepository bansRepository;
    private final ReportRepository reportRepository;
    private final PostResository postRepository;

    /**
     * 대시보드 통계 조회
     */
    @Override
    public DashboardStatsDTO getDashboardStats() {
        log.info("getDashboardStats");
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        long totalUsers = userRepository.count();
        long todayUsers = userRepository.countByCreatedAtAfter(startOfDay);
        long todayPosts = postRepository.countByCreateAtAfter(startOfDay);
        
        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .todayUsers(todayUsers)
                .todayPosts(todayPosts)
                .build();
    }

    /**
     * 전체 유저 목록 조회
     */
    @Override
    public Page<UserSimpleDTO> getAllUsers(int page) {
        log.info("getAllUsers/ page: {}", page);
        Pageable pageable = PageRequest.of(page, 20);
        Page<Users> usersPage = userRepository.findAll(pageable);
        
        List<UserSimpleDTO> userList = usersPage.getContent().stream()
                .map(user -> UserSimpleDTO.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .profileImage(user.getProfileImage())
                        .build())
                .collect(Collectors.toList());
        
        return new PageImpl<>(userList, pageable, usersPage.getTotalElements());
    }

    /**
     * 상태별 유저 목록 조회 (드롭다운 필터용)
     */
    @Override
    public Page<AdminUserDTO> getAdminUsersByStatus(int page, String status) {
        log.info("getAdminUsersByStatus/ page: {}, status: {}", page, status);
        Pageable pageable = PageRequest.of(page, 20);
        LocalDateTime now = LocalDateTime.now();
        
        if ("BANNED".equals(status)) {
            // 정지된 유저만 Bans 테이블에서 직접 조회
            Page<Bans> bansPage = bansRepository.findByFinishedAtAfterOrderByCreatedAtDesc(now, pageable);
            
            List<AdminUserDTO> bannedUsers = bansPage.getContent().stream()
                    .map(ban -> {
                        Users user = ban.getUser();
                        return AdminUserDTO.builder()
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .profileImage(user.getProfileImage())
                                .status("BANNED")
                                .banReason(ban.getCategory())
                                .banFinishedAt(ban.getFinishedAt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                .build();
                    })
                    .collect(Collectors.toList());
            
            return new PageImpl<>(bannedUsers, pageable, bansPage.getTotalElements());
            
        } else if ("ACTIVE".equals(status)) {
            // ACTIVE - 활성 유저만 조회
            Page<Users> usersPage = userRepository.findAll(pageable);
            
            List<AdminUserDTO> activeUsers = usersPage.getContent().stream()
                    .filter(user -> {
                        Optional<Bans> activeBan = bansRepository.findByUser_UserIdAndFinishedAtAfter(
                                user.getUserId(), now);
                        return activeBan.isEmpty();
                    })
                    .map(user -> AdminUserDTO.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .profileImage(user.getProfileImage())
                            .status("ACTIVE")
                            .banReason(null)
                            .banFinishedAt(null)
                            .build())
                    .collect(Collectors.toList());
            
            return new PageImpl<>(activeUsers, pageable, usersPage.getTotalElements());
            
        } else {
            // ALL - 전체 유저
            Page<Users> usersPage = userRepository.findAll(pageable);
            
            List<AdminUserDTO> allUsers = usersPage.getContent().stream()
                    .map(user -> {
                        Optional<Bans> activeBan = bansRepository.findByUser_UserIdAndFinishedAtAfter(
                                user.getUserId(), now);
                        
                        if (activeBan.isPresent()) {
                            return AdminUserDTO.builder()
                                    .userId(user.getUserId())
                                    .username(user.getUsername())
                                    .email(user.getEmail())
                                    .profileImage(user.getProfileImage())
                                    .status("BANNED")
                                    .banReason(activeBan.get().getCategory())
                                    .banFinishedAt(activeBan.get().getFinishedAt()
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build();
                        } else {
                            return AdminUserDTO.builder()
                                    .userId(user.getUserId())
                                    .username(user.getUsername())
                                    .email(user.getEmail())
                                    .profileImage(user.getProfileImage())
                                    .status("ACTIVE")
                                    .banReason(null)
                                    .banFinishedAt(null)
                                    .build();
                        }
                    })
                    .collect(Collectors.toList());
            
            return new PageImpl<>(allUsers, pageable, usersPage.getTotalElements());
        }
    }

    /**
     * 유저 정지
     */
    @Override
    public BansDTO suspendUser(Long userId, int days, String category, String detail) {
        log.info("suspendUser/ userId: {}, days: {}, category: {}", userId, days, category);
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        
        // 정지 종료 날짜 계산
        LocalDateTime finishedAt;
        if (days == -1) {
            // 영구 정지
            finishedAt = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        } else {
            finishedAt = LocalDateTime.now().plusDays(days);
        }
        
        // 정지 기록 생성
        Bans ban = Bans.builder()
                .user(user)
                .category(category)
                .detail(detail)
                .finishedAt(finishedAt)
                .build();
        
        Bans savedBan = bansRepository.save(ban);
        
        // 정지는 bans 테이블만 사용, state는 건드리지 않음 (탈퇴와 별개)
        
        return BansDTO.builder()
                .banId(savedBan.getBanId())
                .userId(savedBan.getUser().getUserId())
                .category(savedBan.getCategory())
                .detail(savedBan.getDetail())
                .createdAt(savedBan.getCreatedAt())
                .finishedAt(savedBan.getFinishedAt())
                .build();
    }

    /**
     * 유저 정지 해제 (현재 정지만 삭제, 과거 기록 유지)
     */
    @Override
    public void activateUser(Long userId) {
        log.info("activateUser/ userId: {}", userId);
        
        // 현재 정지 중인 것만 찾아서 삭제
        Optional<Bans> activeBan = bansRepository.findByUser_UserIdAndFinishedAtAfter(userId, LocalDateTime.now());
        
        if (activeBan.isPresent()) {
            bansRepository.deleteById(activeBan.get().getBanId());
            log.info("유저 현재 정지 해제 완료: userId={}, banId={}", userId, activeBan.get().getBanId());
        } else {
            log.warn("해제할 정지 내역이 없습니다: userId={}", userId);
        }
    }

    /**
     * 유저 탈퇴 처리 (소프트 삭제)
     */
    @Override
    public UserResponseDTO deactivateUser(Long userId) {
        log.info("deactivateUser/ userId: {}", userId);
        return userService.deactivateUser(userId);
    }

    /**
     * 유저 정지 내역 조회
     */
    @Override
    public List<BansDTO> getUserBans(Long userId) {
        log.info("getUserBans/ userId: {}", userId);
        return bansRepository.findByUser_UserId(userId).stream()
                .map(ban -> BansDTO.builder()
                        .banId(ban.getBanId())
                        .userId(ban.getUser().getUserId())
                        .category(ban.getCategory())
                        .detail(ban.getDetail())
                        .createdAt(ban.getCreatedAt())
                        .finishedAt(ban.getFinishedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 관리자용 신고 목록 조회 (페이징) - 정지 이력 포함
     */
    @Override
    public Page<AdminReportDTO> getAdminReports(ReportType type, int page) {
        log.info("getAdminReports/ type: {}, page: {}", type, page);
        Pageable pageable = PageRequest.of(page, 20);
        Page<Reports> reportsPage = reportRepository.findByReportType(type, pageable);
        
        List<AdminReportDTO> reportList = reportsPage.getContent().stream()
                .map(report -> {
                    // 대상 유저의 정지 횟수 조회
                    int banCount = bansRepository.findByUser_UserId(report.getTargetId()).size();
                    
                    // 대상 유저 username 조회
                    String targetUsername = userRepository.findById(report.getTargetId())
                            .map(Users::getUsername)
                            .orElse("삭제된 유저");
                    
                    return AdminReportDTO.builder()
                            .reportId(report.getReportId())
                            .userId(report.getUser().getUserId())
                            .username(report.getUser().getUsername())
                            .reportType(report.getReportType())
                            .targetId(report.getTargetId())
                            .targetUsername(targetUsername)
                            .content(report.getContent())
                            .createdAt(report.getCreatedAt())
                            .banCount(banCount)
                            .build();
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(reportList, pageable, reportsPage.getTotalElements());
    }

    /**
     * 신고 승인 (유저 정지 + 신고 삭제)
     */
    @Override
    public void approveReport(Long reportId, int days) {
        log.info("approveReport/ reportId: {}, days: {}", reportId, days);
        Reports report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));
        
        // 신고 대상 유저 정지
        Long targetUserId = report.getTargetId();
        String category = "신고 승인: " + report.getContent();
        suspendUser(targetUserId, days, category, "관리자 신고 승인");
        
        // 신고 삭제
        reportRepository.delete(report);
        log.info("신고 승인 및 유저 정지 완료: reportId={}, targetUserId={}, days={}", reportId, targetUserId, days);
    }

    /**
     * 신고 삭제 (처리 완료)
     */
    @Override
    public void deleteReport(Long reportId) {
        log.info("deleteReport/ reportId: {}", reportId);
        Reports report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));
        reportRepository.delete(report);
        log.info("신고 삭제 완료: reportId={}", reportId);
    }
}
