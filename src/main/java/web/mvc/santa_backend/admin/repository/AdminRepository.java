package web.mvc.santa_backend.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.admin.entity.AdminEntity;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    List<AdminEntity> findByUser_UserId(Long userId);
}
