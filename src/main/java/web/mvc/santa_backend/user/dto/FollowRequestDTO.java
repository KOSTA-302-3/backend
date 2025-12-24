package web.mvc.santa_backend.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDTO {
    private Long followingId;   // 누구를 팔로우 하는지 (targetId)
}