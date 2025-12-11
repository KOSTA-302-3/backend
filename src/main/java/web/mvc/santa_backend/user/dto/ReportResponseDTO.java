package web.mvc.santa_backend.user.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.ReportType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private Long reportId;
    private UserSimpleDTO user;
    private ReportType reportType;
    private Long targetId;
    private String content;
    private LocalDateTime createdAt;
}
