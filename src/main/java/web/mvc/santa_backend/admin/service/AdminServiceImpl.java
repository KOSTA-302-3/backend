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
     * 신고관리 유저 목록 조회 (정지 상태 포함)
     */
    @Override
    public Page<AdminUserDTO> getAdminUsers(int page) {
        log.info("getAdminUsers/ page: {}", page);
        Pageable pageable = PageRequest.of(page, 20);
        Page<Users> usersPage = userRepository.findAll(pageable);
        
        List<AdminUserDTO> userList = usersPage.getContent().stream()
                .map(user -> {
                    // 정지 상태 확인
                    Optional<Bans> activeBan = bansRepository.findByUser_UserIdAndFinishedAtAfter(
                            user.getUserId(), LocalDateTime.now());
                    
                    String status = "ACTIVE";
                    String banReason = null;
                    String banFinishedAt = null;
                    
                    if (activeBan.isPresent()) {
                        status = "BANNED";
                        banReason = activeBan.get().getCategory();
                        banFinishedAt = activeBan.get().getFinishedAt()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    }
                    
                    return AdminUserDTO.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .profileImage(user.getProfileImage())
                            .status(status)
                            .banReason(banReason)
                            .banFinishedAt(banFinishedAt)
                            .build();
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(userList, pageable, usersPage.getTotalElements());
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
     * 유저 정지 해제 (정지 내역 삭제)
     */
    @Override
    public void activateUser(Long userId) {
        log.info("activateUser/ userId: {}", userId);
        List<Bans> userBans = bansRepository.findByUser_UserId(userId);
        if (!userBans.isEmpty()) {
            bansRepository.deleteAll(userBans);
            log.info("유저 정지 내역 삭제 완료: userId={}", userId);
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
