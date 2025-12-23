package web.mvc.santa_backend.post.dto;

import lombok.*;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.ImageSources;
import web.mvc.santa_backend.post.entity.Posts;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private Long postId;
    private String userProfileImage;
    private String createUserName;
    private LocalDateTime createAt;
    private String content;
    private Long likeCount;
    private Long postLevel;
    private boolean contentVisible;

    private List<String> hashTagsList;
    private List<String> imageSourcesList;


}
