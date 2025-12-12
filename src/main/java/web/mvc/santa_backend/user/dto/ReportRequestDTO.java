package web.mvc.santa_backend.user.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.ReportType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    private ReportType reportType;
    private Long targetId;
    private String content;
}
