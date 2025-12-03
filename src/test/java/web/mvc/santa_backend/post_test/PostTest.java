package web.mvc.santa_backend.post_test;

import org.hibernate.grammars.hql.HqlParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.service.PostService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class PostTest {

    @Autowired
    PostService postService;
    @Autowired
    PostResository postResository;


    @Test
    @DisplayName("필터 Off 전체 게시물 조회")
    void filterOffAll() {
        System.out.println("테스트결과 : " + postService.getAllPostsWithOffFilter());
    }

    @Test
    @DisplayName("유저 ID 기반 게시물 작성")
    void filterOnAll() {
        System.out.println("테스트결과 : " + postService.getAllPostsWithOnFilter(1L));

    }
    @Test
    @DisplayName("유저 ID 기반 게시물 작성")
    void createPost() {


        postResository.save(Posts.builder().
                createUserId(1L)
                .create_at(
                        LocalDateTime.now()
                ).likeCount(0L).postLevel(0L).contentVisible(false).
                build()
        );
        System.out.println("작성 성공 !!");
    }


    @Test
    @DisplayName("유저 ID 기반 게시물 작성")
    void updatePost() {


        postResository.save(Posts.builder().
                postId(10L).
                createUserId(1L).
                create_at(
                        LocalDateTime.now()
                ).likeCount(3L).postLevel(7L).contentVisible(false).
                build()
        );
        System.out.println("작성 성공 !!");
    }

    @Test
    @DisplayName("유저 ID 기반 게시물 작성")
    void deletePost() {


       postResository.deleteById(11L);
        System.out.println("삭제 성공 !!");
    }


}
