package web.mvc.santa_backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 전용 유저 DTO
 * 관리자 페이지에서 유저 목록 표시용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String profileImage;
    private String status;        // ACTIVE, BANNED
    private String banReason;     // 정지 사유 (정지 상태일 때)
    private String banFinishedAt; // 정지 종료일 (정지 상태일 때)
}
