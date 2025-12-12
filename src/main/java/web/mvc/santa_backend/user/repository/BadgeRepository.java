package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Badges;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badges, Long> {
    /**
     * 해당 배지 조회
     */
    Optional<Badges> findByBadgeId(Long badgeId);

    /**
     * 전체 배지 목록 조회 (페이징)
     */
    //Page<Badges> findAll(Pageable pageable);
}
