package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.common.enumtype.ReportType;
import web.mvc.santa_backend.user.entity.Blocks;
import web.mvc.santa_backend.user.entity.Reports;

@Repository
public interface ReportRepository extends JpaRepository<Reports, Long> {
    /**
     * 신고 된 상태인지 확인
     */
    boolean existsByUser_UserIdAndReportTypeAndTargetId(Long userId, ReportType reportType, Long targetId);

    /**
     * 로그인 한 유저의 신고 목록 보기 (페이징)
     * @param userId : 로그인 한 유저
     */
    Page<Reports> findByUser_UserIdAndReportType(Long userId, ReportType type, Pageable pageable);

    /**
     * 전체 신고 목록 보기 (관리자용) (페이징)
     */
    Page<Reports> findAll(Pageable pageable);
}
