package web.mvc.santa_backend.user.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorDTO {
    private Long colorId;
    private String name;
    private String description;
    private String color;
    private int price;
}
