package web.mvc.santa_backend.post.entity.dbtest;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.ImageSources;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "posts")
@Builder
public class MongoTestEntity {

    @Id
    private String id;
    private Long postId;
    private Long createUserId;
    private LocalDateTime create_at;
    private String content;
    private Long likeCount;
    private Long postLevel;
    private boolean contentVisible;
    private List<ImageSources> imgList;
    private List<HashTags> hashTagsList;


}
