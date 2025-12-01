package web.mvc.santa_backend.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Colors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "color_id")
    private Long colorId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 100)
    private String color;

    @Column(nullable = false)
    private int price;
}
