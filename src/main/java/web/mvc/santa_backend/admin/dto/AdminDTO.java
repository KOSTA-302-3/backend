package web.mvc.santa_backend.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private Long banId;
    private Long userId;
    private String category;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
}
