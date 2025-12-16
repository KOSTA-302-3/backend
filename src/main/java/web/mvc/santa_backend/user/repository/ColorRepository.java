package web.mvc.santa_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Colors;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Colors, Long> {
    /**
     * 해당 색상 조회
     */
    Optional<Colors> findByColorId(Long colorId);
}
