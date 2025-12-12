package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Users_Badges;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<Users_Badges, Long> {
    /**
     * 보유하고 있는지 확인
     */
    Optional<Users_Badges> findByUser_UserIdAndBadge_BadgeId(Long userId, Long badgeId);

    /**
     * 로그인 한 유저가 보유한 배지 목록 조회
     */
    @Query(value = "select ub from Users_Badges ub join fetch ub.badge where ub.user.userId = :userId",
            countQuery = "select count(ub) from Users_Badges ub where ub.user.userId = :userId")
    Page<Users_Badges> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 배지 구매
     */
    //save
}
