package web.mvc.santa_backend.chat.service;

import web.mvc.santa_backend.chat.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    public List<NotificationDTO> getAllNotifications();

    public NotificationDTO getNotificationById(Long id);

    public int createNotification(NotificationDTO notificationDTO);

    public int updateNotification(NotificationDTO notificationDTO);

    public int deleteNotificationById(Long id);
}
