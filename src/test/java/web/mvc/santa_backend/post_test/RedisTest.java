package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.RedisTestEntity;
import web.mvc.santa_backend.post.repository.EntityRepository;
import web.mvc.santa_backend.post.repository.PostResository;

import java.util.List;

@SpringBootTest
public class RedisTest {

    @Autowired
    EntityRepository entityRepository;
    @Autowired
    PostResository postResository;


    @Test
   public void redisTest() {
//        List<Posts> list = postResository.findAll();
//        Long i = 0L;
//        for (Posts post : list) {
//            RedisTestEntity redisTestEntity = RedisTestEntity.builder()
//                    .posts(
//                            post
//                    )
//                    .id(post.getPostId())
//                    .build();
//            entityRepository.save(redisTestEntity);
//        i++;
//    }


        Long st = System.currentTimeMillis();
        System.out.println(entityRepository.findById(90004L).get().posts.getContent());

        System.out.println(System.currentTimeMillis()-st);


    }


}
