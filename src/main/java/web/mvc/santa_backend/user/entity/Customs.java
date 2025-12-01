package web.mvc.santa_backend.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customs {
    @Id
    private Long userId;

    /** 유저가 현재 장착한 글자색 */
    @ManyToOne
    @JoinColumn(name = "color_id")
    private Colors color;

    /** 유저 현재 장착한 배지 */
    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badges badge;
}
