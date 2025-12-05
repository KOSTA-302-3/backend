package web.mvc.santa_backend.post.repository;

import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.RedisTestEntity;

import java.util.List;

@Repository
public interface EntityRepository extends CrudRepository<RedisTestEntity, Long> {

    List<RedisTestEntity> findAll();

}