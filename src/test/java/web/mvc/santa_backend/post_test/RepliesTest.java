package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;
import web.mvc.santa_backend.post.service.PostService;
import web.mvc.santa_backend.post.service.RepliesService;

@SpringBootTest
public class RepliesTest {

    @Autowired
    private RepliesService repliesService;
    @Autowired
    private RepliesRepository repliesRepository;
    @Autowired
    private PostResository postResository;



    @Test
    @DisplayName("글번호 해당 댓글 조회 ")
    void repliesGetTest(){

        System.out.println(repliesService.findReplies(1L).size());
        System.out.println(repliesService.findReplies(2L).size());

    }


    @Test
    @DisplayName("댓글 작성")
    void repliesCreateTest(){
        repliesRepository.save(
                Replies.builder().
                        userId(1L).
                        posts(postResository.findById(1L).get()).
                        replyContent("ssssssssssss").replyLike(0L).
                        build()

        );

        System.out.println("댓글 작성 성공!");
    }


    @Test
    @DisplayName("댓글 수정")
    void repliesUpdateTest(){
        repliesRepository.save(
                Replies.builder().
                        replyId(3L).
                        userId(1L).
                        posts(postResository.findById(1L).get()).
                        replyContent("ssssssssssss22222222").replyLike(0L).
                        build()

        );

        System.out.println("댓글 작성 성공!");
    }



    @Test
    @DisplayName("댓글 삭제")
    void repliesDeleteTest(){
        repliesRepository.deleteById(2L);
        System.out.println("댓글 삭제 성공!");
    }


}
