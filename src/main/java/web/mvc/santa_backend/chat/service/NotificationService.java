package web.mvc.santa_backend.chat.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.chat.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    /**
     * 관리자..에서 사용할 지 몰라서 지우진 않았으나 아직 구현되지 않은 메서드
     * @return
     */
    public List<NotificationDTO> getAllNotifications();

    /**
     * user가 읽지 않은 알림만 가져오는 메서드
     * @param id
     * @param page
     * @return
     */
    public Page<NotificationDTO> getNotificationByUserId(Long id, int page);

    /**
     * 유저의 모든 알림을 가져오는 메서드
     * @param id
     * @param page
     * @return
     */

    public Page<NotificationDTO> getAllNotificationByUserId(Long id, int page);

    /**
     * 알림을 생성하는 메서드.
     * DTO내의 필수입력 변수목록
     * Long userId(알림을 볼 사람)
     * Long actionUserId(좋아요를 누른 사람, DM을 보낸 사람, 답글을 단 사람..)
     * NotificationType type(enum)
     * 선택입력
     * String link(url)
     * @param notificationDTO
     * @return
     */
    public void createNotification(NotificationDTO notificationDTO);

    /**
     * isRead의 값을 true로 바꿔서 읽은 상태로 바꾸는 메서드
     * @param id
     */
    public void deleteNotificationById(Long id);
}
