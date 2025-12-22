package web.mvc.santa_backend.post.repository.dbtest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.dbtest.RedisTestEntity;

import java.util.List;

@Repository
public interface EntityRepository extends CrudRepository<RedisTestEntity, Long> {

    List<RedisTestEntity> findAll();

}