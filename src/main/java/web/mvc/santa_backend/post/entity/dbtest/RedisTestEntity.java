package web.mvc.santa_backend.post.entity.dbtest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import web.mvc.santa_backend.post.entity.Posts;

import java.util.List;

@Getter
@Builder
@RedisHash(value = "test")
public class RedisTestEntity {

    @Id
    public Long id;
    public Long job_id;
    public List<String> image_urls;
    public String content;


}
