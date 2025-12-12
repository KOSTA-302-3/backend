package web.mvc.santa_backend.admin.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.admin.dto.AdminDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

public interface AdminService {
    
    /**
     * 전체 유저 목록 조회 (페이징)
     */
    Page<UserSimpleDTO> getAllUsers(int page);
    
    /**
     * 유저 상세 조회
     */
    UserResponseDTO getUserDetail(Long userId);
    
    /**
     * 유저 정지 (기간별)
     */
    AdminDTO suspendUser(Long userId, int days, String category, String detail);
    
    /**
     * 유저 정지 해제 (state = true)
     */
    void activateUser(Long userId);
    
    /**
     * 유저 탈퇴 처리 (소프트 삭제)
     */
    UserResponseDTO deactivateUser(Long userId);
    
    /**
     * 유저 정지 내역 조회
     */
    List<AdminDTO> getUserBans(Long userId);
}
