package web.mvc.santa_backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "간단한 유저 정보 조회(팔로우 목록, 게시물 상단, DM 상단 등) 시 사용되는 UserDTO")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO {
    private Long userId;
    private String username;
    private String profileImage;
    private CustomDTO customDTO;
}
