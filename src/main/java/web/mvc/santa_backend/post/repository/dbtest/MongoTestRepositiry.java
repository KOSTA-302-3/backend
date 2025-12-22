package web.mvc.santa_backend.post.repository.dbtest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.dbtest.MongoTestEntity;

@Repository
public interface MongoTestRepositiry extends MongoRepository<MongoTestEntity,String> {

    MongoTestEntity findByPostId(Long id);

    MongoTestEntity findByContent(String st);

}
