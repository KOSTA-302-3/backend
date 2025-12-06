package web.mvc.santa_backend.post.dto;

import lombok.*;
import web.mvc.santa_backend.post.entity.Replies;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepliesDTO {
    private Long replyId;

    private Long userId;

    private Long postId;

    private String replyContent;

    private Long replyLike;
}
