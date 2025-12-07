package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.dbtest.MongoTestEntity;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;

import java.util.List;

@SpringBootTest
public class MongoTest {

    @Autowired
    PostResository postResository;
    @Autowired
    MongoRepository mongoRepository;
    @Autowired
    HashTagsRepository hashTagsRepository;
    @Autowired
    ImageSourcesRepository imageSourcesRepository;

    @Test
    void insertDataToMongo() {

        List<Posts> list = postResository.findAll();
        long st = System.currentTimeMillis();
        System.out.println("이식 시작");
        for (Posts post : list) {

            mongoRepository.save(MongoTestEntity.builder()
                            .postId(post.getPostId())
                            .create_at(post.getCreate_at())
                            .createUserId(post.getCreateUserId())
                            .content(post.getContent())
                            .likeCount(post.getLikeCount())
                            .postLevel(post.getPostLevel())
                            .contentVisible(post.isContentVisible())
                            .imgList(imageSourcesRepository.findAllByPostsPostId(post.getPostId()))
                            .hashTagsList(hashTagsRepository.findAllByPostsPostId(post.getPostId()))

                    .build()
            );


        }

        System.out.println(System.currentTimeMillis()-st);
        System.out.println("이식 완료");

    }

}
