package web.mvc.santa_backend.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.ImageSources;
import web.mvc.santa_backend.post.entity.Posts;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class PostDTO {
    private  Posts posts;

    private List<HashTags> hashTagsList;
    private List<ImageSources> imageSourcesList;




}
