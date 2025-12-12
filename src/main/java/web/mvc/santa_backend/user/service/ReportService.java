package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.user.dto.ReportRequestDTO;
import web.mvc.santa_backend.user.dto.ReportResponseDTO;

public interface ReportService {
    /**
     * 신고
     */
    ReportResponseDTO report(Long userId, ReportRequestDTO reportRequestDTO);

    /**
     * 신고 확인
     */
    boolean isReporting(Long userId, ReportType type, Long targetId);

    /**
     * 로그인 한 유저의 신고 목록 조회 (페이징)
     */
    Page<Object> getReportsByUserId(Long userId, ReportType type, int page);

    /**
     * 전체 신고 목록 조회 (관리자용) (페이징)
     */
    Page<Object> getAllReports(ReportType type, int page);
}
