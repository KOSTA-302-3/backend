package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.common.enumtype.CustomItemType;
import web.mvc.santa_backend.user.dto.CustomDTO;
import web.mvc.santa_backend.user.entity.Customs;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.entity.Users_Badges;
import web.mvc.santa_backend.user.entity.Users_Colors;

public interface CustomService {
    /**
     * id 에 해당하는 Custom 등록 (회원가입 시 최초 1회 실행)
     * 등록된 엔티티 반환(확인용)
     */
    CustomDTO addCustom(Users user);

    /**
     * Custom 변경
     */
    CustomDTO updateCustom(CustomItemType type, Long userId, Long targetId);
}
