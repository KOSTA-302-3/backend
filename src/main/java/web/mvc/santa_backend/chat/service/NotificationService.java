package web.mvc.santa_backend.chat.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.chat.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    public List<NotificationDTO> getAllNotifications();

    public Page<NotificationDTO> getNotificationByUserId(Long id, int page);

    /**
     * 알림을 생성하는 메서드.
     * DTO내의 필수입력 변수목록
     * Long userId
     * NotificationType type(enum)
     * 선택입력
     * String link(url)
     * @param notificationDTO
     * @return
     */
    public void createNotification(NotificationDTO notificationDTO);

    public void updateNotification(NotificationDTO notificationDTO);

    public void deleteNotificationById(Long id);
}
