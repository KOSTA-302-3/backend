package web.mvc.santa_backend.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.mvc.santa_backend.chat.entity.Messages;

public interface MessageRepository extends JpaRepository<Messages,Long> {
}
