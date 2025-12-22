package web.mvc.santa_backend.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private Long badgeId;
    private String name;
    private String description;
    private String imageUrl;
    private int price;
}
