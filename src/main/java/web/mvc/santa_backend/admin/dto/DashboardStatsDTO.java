package web.mvc.santa_backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalUsers;       // 총 가입자 수
    private long todayUsers;       // 오늘 가입한 유저 수
    private long todayPosts;       // 오늘 올라온 게시글 수
}
