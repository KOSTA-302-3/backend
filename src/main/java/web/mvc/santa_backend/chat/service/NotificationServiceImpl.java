package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.entity.Notifications;
import web.mvc.santa_backend.chat.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getAllNotifications() {
        return List.of();
    }

    @Override
    public NotificationDTO getNotificationById(Long id) {
        return null;
    }

    @Override
    public int createNotification(NotificationDTO notificationDTO) {

        return 0;
    }

    @Override
    public int updateNotification(NotificationDTO notificationDTO) {
        return 0;
    }

    @Override
    public int deleteNotificationById(Long id) {
        return 0;
    }

    private Notifications toEntity(NotificationDTO notificationDTO){

        return Notifications.builder().build();
    }

    private NotificationDTO toDTO(Notifications notifications){
        return NotificationDTO.builder().build();
    }
}
