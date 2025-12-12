package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Users_Colors;

import java.util.Optional;

@Repository
public interface UserColorRepository extends JpaRepository<Users_Colors, Long> {
    /**
     * 보유하고 있는지 확인
     */
    Optional<Users_Colors> findByUser_UserIdAndColor_ColorId(Long userId, Long colorId);

    /**
     * 로그인 한 유저가 보유한 색상 목록 조회
     */
    @Query(value = "select uc from Users_Colors uc join fetch uc.color where uc.user.userId = :userId",
            countQuery = "select count(uc) from Users_Colors uc where uc.user.userId = :userId")
    Page<Users_Colors> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 색상 구매
     */
    //save
}
