package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import web.mvc.santa_backend.post.entity.dbtest.RedisPosts;
import web.mvc.santa_backend.post.repository.dbtest.EntityRepository;
import web.mvc.santa_backend.post.repository.PostResository;

@SpringBootTest
public class RedisTest {

    @Autowired
    EntityRepository entityRepository;
    @Autowired
    PostResository postResository;
    @Autowired
    RedisTemplate<String,RedisPosts> redisTemplate;


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


//        Long st = System.currentTimeMillis();
//        System.out.println(entityRepository.findById(90004L).get().posts.getContent());
//
//        System.out.println(System.currentTimeMillis()-st);
        RedisPosts redisPosts = new RedisPosts(11L,null,null);
        redisTemplate.opsForList().rightPush("queue:inference", redisPosts);
    }


}
