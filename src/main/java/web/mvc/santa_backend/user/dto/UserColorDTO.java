package web.mvc.santa_backend.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserColorDTO {
    private Long id;
    private Long userId;
    private Long colorId;
    private LocalDateTime createdAt;
}
