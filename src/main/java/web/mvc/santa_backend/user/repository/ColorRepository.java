package web.mvc.santa_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Colors;

@Repository
public interface ColorRepository extends JpaRepository<Colors, Long> {

}
