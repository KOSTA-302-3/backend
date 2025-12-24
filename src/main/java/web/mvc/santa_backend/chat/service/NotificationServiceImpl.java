package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.dto.NotificationResponseDTO;
import web.mvc.santa_backend.chat.entity.Notifications;
import web.mvc.santa_backend.chat.manager.NotificationManager;
import web.mvc.santa_backend.chat.repository.NotificationRepository;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.UserNotFoundException;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getNotificationByUserId(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Notifications> notifications = notificationRepository.findByUserAndIsRead(Users.builder().userId(id).build(), false, pageable);

        Page<NotificationResponseDTO> notificationDTOS = notifications.map(n -> toDTO(n));
        return notificationDTOS;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getAllNotificationByUserId(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Notifications> notifications = notificationRepository.findByUser(Users.builder().userId(id).build(), pageable);
        Page<NotificationResponseDTO> notificationDTOS = notifications.map(n -> toDTO(n));
        return notificationDTOS;
    }

    @Override
    public void createNotification(NotificationDTO notificationDTO) {
        //TODO
        //user 테이블에서 userId로 user를 불러와서.. username이나 이런정보를 불러와야 할 듯
        //일단 임시로  userId로만 처리
        StringBuilder sb = new StringBuilder();
        //알림 타입에 따라 메시지 분기처리 를.. 해야할지..
        switch (notificationDTO.getType()) {
            case DM -> sb.append(notificationDTO.getActionUserId()).append("님의 DM");
            case TAG -> sb.append(notificationDTO.getActionUserId()).append("님의 태그");
            case LIKE -> sb.append(notificationDTO.getActionUserId()).append("님의 좋아요");
            case POST -> sb.append(notificationDTO.getActionUserId()).append("님의 포스트");
            case REPLY -> sb.append(notificationDTO.getActionUserId()).append("님의 답글");
            case FOLLOW -> sb.append(notificationDTO.getActionUserId()).append("님의 팔로우");
        }
        String messsage = sb.toString();

        notificationDTO.setMessage(messsage);
        Users user = Users.builder().userId(notificationDTO.getUserId()).build();
        Users actionUser = Users.builder().userId(notificationDTO.getActionUserId()).build();
        Notifications notification = toEntity(notificationDTO, user, actionUser);
        notificationRepository.save(notification);
        log.info("여기까지 오면 send됨");
        notificationManager.sendNewNotification(notificationDTO.getUserId());
    }


    @Override
    public void deleteNotificationById(Long id) {
        Notifications notification = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException());
        notification.setRead(true);
    }

    @Override
    public void deleteAllNotificationById(Long userId) {
        List<Notifications> unreadList = notificationRepository.findByUser_UserIdAndIsRead(userId, false);
        for (Notifications notification : unreadList) {
            notification.setRead(true);
        }
    }

    @Override
    public long countNotificationByUserId(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsRead(userId, false);
    }

    /**
     * DTO를 Entity로 바꾸는 맵퍼 메서드
     * @param notificationDTO
     * @param user
     * @return
     */
    private Notifications toEntity(NotificationDTO notificationDTO, Users user, Users actionUser) {
        return Notifications.builder()
                .user(user)
                .message(notificationDTO.getMessage())
                .link(notificationDTO.getLink())
                .notificationType(notificationDTO.getType())
                .actionUser(actionUser)
                .build();
    }

    /**
     * Entity를 DTO로 바꾸는 맵퍼 메서드
     * @param notifications
     * @return
     */
    private NotificationResponseDTO toDTO(Notifications notifications){
        Users actionUser = userRepository.findById(notifications.getActionUser().getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        StringBuilder messageBuilder = new StringBuilder();
        String title = null;
        //알림 타입에 따라 메시지 분기처리 를.. 해야할지..
        switch (notifications.getNotificationType()) {
            case DM -> {
                messageBuilder.append(actionUser.getUsername()).append("님의 DM");
                title = "새로운 메시지";
            }
            case TAG -> {
                messageBuilder.append(actionUser.getUsername()).append("님의 태그");
                title = "새로운 태그";
            }
            case LIKE -> {
                messageBuilder.append(actionUser.getUsername()).append("님의 좋아요");
                title = "새로운 좋아요";
            }
            case POST -> {
                messageBuilder.append(actionUser.getUsername()).append("님의 포스트");
                title = "새로운 포스트";
            }
            case REPLY -> {
                messageBuilder.append(actionUser.getUsername()).append("님의 답글");
                title = "새로운 답글";
            }
            case FOLLOW -> {
                messageBuilder.append(actionUser.getUsername()).append("님의 팔로우");
                title = "새로운 팔로우";
            }
        }
        String message = messageBuilder.toString();
        return NotificationResponseDTO.builder()
                .id(notifications.getNotificationId())
                .title(title)
                .message(message)
                .time(notifications.getCreatedAt())
                .isUnread(!notifications.isRead())
                .build();
    }


}
