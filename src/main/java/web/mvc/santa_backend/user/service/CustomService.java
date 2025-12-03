package web.mvc.santa_backend.user.service;

import web.mvc.santa_backend.user.dto.CustomDTO;
import web.mvc.santa_backend.user.entity.Customs;
import web.mvc.santa_backend.user.entity.Users;

public interface CustomService {
    /**
     * id 에 해당하는 유저 Custom 조회
     */
    CustomDTO getCustomById(Long id);

    /**
     * id 에 해당하는 Custom 등록 (회원가입 시 최초 1회 실행)
     * 등록된 엔티티 반환(확인용)
     */
    Customs addCustom(Users user);

    /**
     * Custom 변경
     */
    CustomDTO updateCustom();

    // 유저가 보유한 Custom 목록..및 구매하면 추가...
}
