package web.mvc.santa_backend.post.dto;

import lombok.*;
import web.mvc.santa_backend.post.entity.Replies;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepliesReponseDTO {
    private Long replyId;

    private String createUserName;

    private Long postId;

    private String replyContent;

    private Long replyLike;

    private String userProfileImage;
}
