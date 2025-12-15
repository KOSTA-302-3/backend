package web.mvc.santa_backend.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.admin.entity.Bans;

import java.util.List;

@Repository
public interface BansRepository extends JpaRepository<Bans, Long> {
    List<Bans> findByUser_UserId(Long userId);
}
