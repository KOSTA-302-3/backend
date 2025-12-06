package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.santa_backend.post.repository.HashTagsRepository;

@SpringBootTest
public class HashTest {
    @Autowired
    private HashTagsRepository hashTagsRepository;
    @Test
    public void hashTest(){
        System.out.println(hashTagsRepository.findAllByPostsPostId(1L));
    };

    @Test
    public void hashTagInserTest(){
        System.out.println(hashTagsRepository.findByTag("#santa").toString());

    }

}
