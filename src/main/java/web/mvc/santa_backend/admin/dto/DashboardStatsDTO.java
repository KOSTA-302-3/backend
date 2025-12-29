package web.mvc.santa_backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalUsers;       // 총 가입자 수
    private long todayUsers;       // 오늘 가입한 유저 수
    private long todayPosts;       // 오늘 올라온 게시글 수
    
    // 최근 7일 통계
    private List<DailyStats> weeklyUserStats;  // 일별 가입자 수
    private List<DailyStats> weeklyPostStats;  // 일별 게시글 수
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private String date;  // 날짜 (MM/DD 형식)
        private long count;   // 해당 날짜의 카운트
    }
}

