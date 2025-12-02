package web.mvc.santa_backend.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.mvc.santa_backend.chat.entity.Notifications;

public interface NotificationRepository extends JpaRepository<Notifications,Long> {
}
