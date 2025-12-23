package web.mvc.santa_backend.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import web.mvc.santa_backend.post.dto.PostDTO;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "유저 정보 조회(개인 페이지) 시 사용되는 UserDTO")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String profileImage;
    private String description;
    private Long followingCount;
    private Long followerCount;

    private Long point;
    private int level;

    @JsonProperty("isPrivate")
    private boolean isPrivate;
    private boolean state;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private CustomDTO customDTO;

    @JsonProperty("isMe")
    private boolean isMe;
}
