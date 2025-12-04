package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    Users findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // 예전 거 삭제예정
    Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query(value = "select u from Users u left join fetch u.custom c " +
            "where lower(u.username) like lower(concat('%', :username, '%'))",
            countQuery = "select count(u.userId) from Users u left join u.custom")
    Page<Users> findWithCustomByUsername(String username, Pageable pageable);

    @Query("select u from Users u left join fetch u.custom c where u.userId = :id")
    Optional<Users> findWithCustomById(Long id);

    /* 팔로우 관련 cnt 증감 */
    @Modifying
    @Query("update Users u set u.followingCount = u.followingCount + 1 where u.userId = :userId")
    void increaseFollowingCount(Long userId);

    @Modifying
    @Query("update Users u set u.followerCount = u.followerCount + 1 where u.userId = :userId")
    void decreaseFollowingCount(Long userId);

    @Modifying
    @Query("update Users u set u.followingCount = u.followingCount - 1 where u.userId = :userId")
    void increaseFollowerCount(Long userId);

    @Modifying
    @Query("update Users u set u.followerCount = u.followerCount - 1 where u.userId = :userId")
    void decreaseFollowerCount(Long userId);
}
