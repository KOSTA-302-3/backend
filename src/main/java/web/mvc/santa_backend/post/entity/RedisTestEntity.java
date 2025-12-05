package web.mvc.santa_backend.post.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Getter
@Builder
@RedisHash(value = "test")
public class RedisTestEntity {

    @Id
    public Long id;
    public Posts posts;


}
