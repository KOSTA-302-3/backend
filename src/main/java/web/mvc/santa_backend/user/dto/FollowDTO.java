package web.mvc.santa_backend.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {
    private Long followId;
    private Long followerId;    // 누가 (userId)
    private Long followingId;   // 누구를 팔로우 하는지 (targetId)
    private boolean pending;
    private LocalDateTime createdAt;
}
