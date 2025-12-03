package web.mvc.santa_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Customs;

@Repository
public interface CustomRepository extends JpaRepository<Customs, Long> {

}
