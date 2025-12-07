package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.service.PostService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

@SpringBootTest
public class PostTest {

    @Autowired
    PostService postService;
    @Autowired
    PostResository postResository;


    @Test
    @DisplayName("필터 Off 전체 게시물 조회")
    void filterOffAll() {
        System.out.println("테스트결과 : " + postService.getAllPostsWithOffFilter(1));
    }

    @Test
    @DisplayName("필터 Off 전체 게시물 조회")
    void filterOffAllOnGraph() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("테스트결과 : " + postService.getAllPostsWithOffFilter(1));
        Thread.sleep(1000);
    }

    @Test
    @DisplayName("유저 ID 기반 게시물 작성")
    void filterOnAll() {
        System.out.println("테스트결과 : " + postService.getAllPostsWithOnFilter(1L,1));

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

    @Test
    void createData() {

//        for (int i = 0; i < 100000; i++) {
//
//            postResository.save(Posts.builder().
//                    createUserId(1L).
//                    create_at(LocalDateTime.now()).
//                    likeCount(0L).
//                    content("testCase" + i).
//                    postLevel(1L).
//                    contentVisible(false).
//                    build());
//        }
    }


    @Test
    void findPostsTestData() {
        long st = System.currentTimeMillis();
        System.out.println(postResository.findById(100001L).get().getContent());
        long ed = System.currentTimeMillis();
        System.out.println(ed - st);

    }

    @Test
    void pageTest() {
        int z = 0;
        boolean ck = true;
    Scanner sc = new Scanner(System.in);
    int j = sc.nextInt();

        while (ck) {
            Pageable pageable = PageRequest.of(j, 5);
            Page<Posts> page = postResository.findAll(pageable);


            for (int i = 0; i < page.getSize(); i++) {
                System.out.println(page.getContent().get(i).getLikeCount());
            }


            switch (j) {
                case 1:
                    j--;
                    break;
                case 2:
                    j++;
                    break;
                default:
                    ck = false;
                    break;
            }

        }

//        System.out.println(page.getTotalPages());
//        System.out.println(page.getTotalElements());
//        long ed =  System.currentTimeMillis();
//        System.out.println(ed-st);


    }

    @Test
    void bb() {
        // 1. 가짜 입력값 설정 (예: "10"을 입력한다고 가정)
        String input = "10";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in); // System.in을 가짜 입력 스트림으로 교체

        // 2. 스캐너 실행
        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();

        System.out.println("입력받은 값: " + i); // 10 출력됨

        // (선택) 테스트가 끝나면 System.in을 원래대로 돌려놓는 것이 좋음
        System.setIn(System.in);
    }


}
