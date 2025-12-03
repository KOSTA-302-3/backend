package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.service.PostService;

import java.util.List;

@SpringBootTest
public class PostTest {

    @Autowired
    PostService postService;


    @Test
    @DisplayName("필터 Off 전체 게시물")
    void filterOffAll (){


        System.out.println("테스트결과 : " + postService.getAllPostsWithOffFilter());




    }


    @Test
    @DisplayName("필터 On 전체 게시물")
    void filterOnAll (){
        System.out.println("테스트결과 : " + postService.getAllPostsWithOnFilter(1L));

    }



}
