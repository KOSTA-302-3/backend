package web.mvc.santa_backend.admin.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.admin.dto.AdminReportDTO;
import web.mvc.santa_backend.admin.dto.AdminUserDTO;
import web.mvc.santa_backend.admin.dto.BansDTO;
import web.mvc.santa_backend.admin.dto.DashboardStatsDTO;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

public interface AdminService {
    
    /**
     * 대시보드 통계 조회
     */
    DashboardStatsDTO getDashboardStats();
    
    /**
     * 전체 유저 목록 조회 (페이징) 
     */
    Page<UserSimpleDTO> getAllUsers(int page);
    
    /**
     * 관리자용 유저 목록 조회 (페이징) - Admin 전용 (정지 상태 포함)
     */
    Page<AdminUserDTO> getAdminUsers(int page);
    
    /**
     * 유저 정지 (기간별)
     */
    BansDTO suspendUser(Long userId, int days, String category, String detail);
    
    /**
     * 유저 정지 해제 (정지 내역 삭제)
     */
    void activateUser(Long userId);
    
    /**
     * 유저 탈퇴 처리 (소프트 삭제)
     */
    UserResponseDTO deactivateUser(Long userId);
    
    /**
     * 유저 정지 내역 조회
     */
    List<BansDTO> getUserBans(Long userId);
    
    /**
     * 관리자용 신고 목록 조회 (페이징) - 정지 이력 포함
     */
    Page<AdminReportDTO> getAdminReports(ReportType type, int page);

    /**
     * 신고 승인 (유저 정지)
     */
    void approveReport(Long reportId, int days);
    
    /**
     * 신고 삭제 (처리 완료)
     */
    void deleteReport(Long reportId);
}
