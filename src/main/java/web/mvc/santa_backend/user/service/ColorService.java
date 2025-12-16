package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.user.dto.BadgeDTO;
import web.mvc.santa_backend.user.dto.ColorDTO;
import web.mvc.santa_backend.user.dto.UserBadgeDTO;
import web.mvc.santa_backend.user.dto.UserColorDTO;

public interface ColorService {
    /**
     * 전체 색상 목록 조회 (페이징)
     */
    Page<ColorDTO> getColors(int page);

    /**
     * 로그인 한 유저가 보유한 색상 목록 조회 (페이징)
     */
    Page<ColorDTO> getColorsByUserId(Long userId, int page);

    /**
     * 색상 구매
     */
    UserColorDTO buyColor(Long userId, Long colorId);

    /**
     * 색상 추가 (관리자용)
     */
    ColorDTO addColor(ColorDTO colorDTO);
}
