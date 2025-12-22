package web.mvc.santa_backend.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.admin.entity.Bans;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BansRepository extends JpaRepository<Bans, Long> {
    List<Bans> findByUser_UserId(Long userId);
    
    // 현재 활성화된 정지 조회 (관리자용)
    Optional<Bans> findByUser_UserIdAndFinishedAtAfter(Long userId, LocalDateTime now);
    
    // 정지 중인 목록 조회 (페이징, 최신순)
    Page<Bans> findByFinishedAtAfterOrderByCreatedAtDesc(LocalDateTime now, Pageable pageable);
}
