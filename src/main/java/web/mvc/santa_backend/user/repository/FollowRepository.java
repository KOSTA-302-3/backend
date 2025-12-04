package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Follows;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follows, Long> {

    /**
     * 팔로잉 전체 목록 보기 (팔로우 한 유저들의 글 보기 위함)
     * (현재 유저가 팔로우 한 사람들(=following_id)을 봐야하므로 follower_id가 현재 유저여야 함)
     */
    List<Follows> findByFollower_UserIdAndFollowing_StateIsTrueAndPendingIsFalse(Long id);

    /**
     * 팔로워 전체 목록 보기 (글 등록 시 팔로워들에게 알림 전달 위함)
     */
    List<Follows> findByFollowing_UserIdAndFollower_StateIsTrueAndPendingIsFalse(Long id);

    /**
     * 팔로잉 목록 보기 (페이징)
     */
    Page<Follows> findByFollower_UserIdAndFollowing_StateIsTrueAndPendingIsFalse(Long id, Pageable pageable);

    /**
     * 팔로워 목록 보기 (페이징)
     * (현재 유저를 팔로우 하고 있는 사람들(=follower_id)을 봐야하므로 follwing_id가 현재 유저여야 함)
     */
    Page<Follows> findByFollowing_UserIdAndFollower_StateIsTrueAndPendingIsFalse(Long id, Pageable pageable);

    /**
     * 팔로우 대기 상태인 유저들 목록 보기 (페이징)
     */
    Page<Follows> findByPendingIsTrue(Pageable pageable);

    Optional<Follows> findByFollower_UserIdAndFollowing_UserId(Long id, Long targetId);

    Boolean existsByFollower_UserIdAndFollowing_UserId(Long id, Long targetId);
}
