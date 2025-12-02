package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.entity.Notifications;
import web.mvc.santa_backend.chat.repository.NotificationRepository;
import web.mvc.santa_backend.user.entity.Users;

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
    public Page<NotificationDTO> getNotificationByUserId(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Notifications> notifications = notificationRepository.findByUser(Users.builder().userId(id).build(), pageable);

        Page<NotificationDTO> notificationDTOS = notifications.map(n -> toDTO(n));
        return notificationDTOS;
    }

    @Override
    public void createNotification(NotificationDTO notificationDTO) {
        //TODO
        //user 테이블에서 userId로 user를 불러와서.. username이나 이런정보를 불러와야 할 듯
        //일단 임시로  userId로만 처리
        StringBuilder sb = new StringBuilder();
        //알림 타입에 따라 메시지 분기처리
        switch (notificationDTO.getType()) {
            case DM -> sb.append(notificationDTO.getUserId()).append("님의 DM");
            case TAG -> sb.append(notificationDTO.getUserId()).append("님의 태그");
            case LIKE -> sb.append(notificationDTO.getUserId()).append("님의 좋아요");
            case POST -> sb.append(notificationDTO.getUserId()).append("님의 포스트");
            case REPLY -> sb.append(notificationDTO.getUserId()).append("님의 답글");
            case FOLLOW -> sb.append(notificationDTO.getUserId()).append("님의 팔로우");
        }
        String messsage = sb.toString();

        notificationDTO.setMessage(messsage);
        Users user = Users.builder().userId(notificationDTO.getUserId()).build();
        Notifications notification = toEntity(notificationDTO, user);
        notificationRepository.save(notification);
    }

    @Override
    public void updateNotification(NotificationDTO notificationDTO) {

    }

    @Override
    public void deleteNotificationById(Long id) {

    }

    private Notifications toEntity(NotificationDTO notificationDTO, Users user){
        return Notifications.builder()
                .user(user)
                .message(notificationDTO.getMessage())
                .link(notificationDTO.getLink())
                .notificationType(notificationDTO.getType())
                .build();
    }

    private NotificationDTO toDTO(Notifications notifications){

        return NotificationDTO.builder()
                .notificationId(notifications.getNotificationId())
                .userId(notifications.getUser().getUserId())
                .message(notifications.getMessage())
                .link(notifications.getLink())
                .isRead(notifications.isRead())
                .type(notifications.getNotificationType())
                .createdAt(notifications.getCreatedAt())
                .build();
    }
}
