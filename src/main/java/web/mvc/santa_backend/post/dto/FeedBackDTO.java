package web.mvc.santa_backend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackDTO {

    private Long feedBackId;
    private Long userId;
    private Long postId;
    private Long level;
    private LocalDateTime createAt;



}
