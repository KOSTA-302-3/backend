package web.mvc.santa_backend.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomDTO {
    private ColorDTO colorDTO;
    private BadgeDTO badgeDTO;
}
