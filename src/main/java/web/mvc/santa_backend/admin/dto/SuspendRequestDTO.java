package web.mvc.santa_backend.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspendRequestDTO {
    private int days;       // 7(7일), 30(30일), 365(1년), -1(영구)
    private String category;
    private String detail;
}
