package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.santa_backend.post.service.PostService;
import web.mvc.santa_backend.post.service.RepliesService;

@SpringBootTest
public class RepliesTest {

    @Autowired
    private RepliesService repliesService;



    @Test
    @DisplayName("글번호 댓글")
    void repliesGetTest(){

        System.out.println(repliesService.findReplies(1L).size());
        System.out.println(repliesService.findReplies(2L).size());

    }


}
