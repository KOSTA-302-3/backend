package web.mvc.santa_backend.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.*;

@Schema(description = "회원가입, 유저 정보 수정 시 사용되는 UserDTO")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    // 가입 시 필수
    private String username;
    private String password;
    private String email;
    private String phone;
    // 가입 시 선택
    private String profileImage;
    private String description;
    private int level;
    // 수정 시
    //@JsonProperty("isPrivate")
    private boolean isPrivate;
    private boolean state;
}
