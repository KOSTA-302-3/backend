package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.user.dto.BadgeDTO;
import web.mvc.santa_backend.user.dto.UserBadgeDTO;

public interface BadgeService {
    /**
     * 전체 배지 목록 조회 (페이징)
     */
    Page<BadgeDTO> getBadges(int page);

    /**
     * 로그인 한 유저가 보유한 배지 목록 조회 (페이징)
     */
    Page<BadgeDTO> getBadgesByUserId(Long userId, int page);

    /**
     * 배지 구매
     */
    UserBadgeDTO buyBadge(Long userId, Long badgeId);
}
