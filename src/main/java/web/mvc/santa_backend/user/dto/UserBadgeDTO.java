package web.mvc.santa_backend.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadgeDTO {
    private Long id;
    private Long userId;
    private Long badgeId;
    private LocalDateTime createdAt;
}
