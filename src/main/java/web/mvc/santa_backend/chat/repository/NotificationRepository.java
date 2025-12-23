package web.mvc.santa_backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import web.mvc.santa_backend.chat.entity.Notifications;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications,Long> {
    Page<Notifications> findByUser(Users user, Pageable pageable);

    Page<Notifications> findByUserAndIsRead(Users user, boolean isRead, Pageable pageable);

    Page<Notifications> findByUser_UserIdAndIsRead(Long userUserId, boolean isRead, Pageable pageable);

    List<Notifications> findByUser_UserIdAndIsRead(Long userUserId, boolean isRead);

    long countByUser_UserIdAndIsRead(Long userUserId, boolean isRead);
}
