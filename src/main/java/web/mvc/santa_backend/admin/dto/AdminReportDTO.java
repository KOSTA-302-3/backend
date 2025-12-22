package web.mvc.santa_backend.admin.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.ReportType;

import java.time.LocalDateTime;

/**
 * 관리자용 신고 DTO (정지 이력 포함)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportDTO {
    private Long reportId;
    private Long userId;           // 신고자 ID
    private String username;       // 신고자 이름
    private ReportType reportType;
    private Long targetId;         // 신고 대상 ID
    private String targetUsername; // 신고 대상 이름
    private String content;        // 신고 사유
    private LocalDateTime createdAt;
    
    // 대상 유저 정지 이력
    private int banCount;          // 총 정지 횟수
}
