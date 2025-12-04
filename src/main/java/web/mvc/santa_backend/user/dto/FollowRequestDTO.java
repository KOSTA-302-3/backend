package web.mvc.santa_backend.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDTO {
    private Long followerId;    // userId 누가    (jwt 적용하면 없어질)
    private Long followingId;   // targetId 누구를 팔로우
}
