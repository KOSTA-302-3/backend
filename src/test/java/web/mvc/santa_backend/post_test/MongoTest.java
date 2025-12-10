package web.mvc.santa_backend.post_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.ImageSources;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.dbtest.MongoTestEntity;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.dbtest.MongoTestRepositiry;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MongoTest {

    @Autowired
    PostResository postResository;
    @Autowired
    MongoTestRepositiry mongoTestRepositiry;
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
            List<String> imgList = new ArrayList<>();
            for(ImageSources img: imageSourcesRepository.findAllByPostsPostId(post.getPostId())){
                imgList.add(img.getSource());
            }

            List<String> hashList = new ArrayList<>();
            for(HashTags hash: hashTagsRepository.findAllByPostsPostId(post.getPostId())){
                hashList.add(hash.getTag());
            }



            mongoTestRepositiry.save(MongoTestEntity.builder()
                            .postId(post.getPostId())
                            .create_at(post.getCreate_at())
                            .createUserId(post.getCreateUserId())
                            .content(post.getContent())
                            .likeCount(post.getLikeCount())
                            .postLevel(post.getPostLevel())
                            .contentVisible(post.isContentVisible())
                            .imgList(imgList)
                            .hashTagsList(hashList)

                    .build()
            );


        }

        System.out.println(System.currentTimeMillis()-st);
        System.out.println("이식 완료");

    }

    @Test
    void deleteAllDataToMongo(){
        mongoTestRepositiry.deleteAll();
        imageSourcesRepository.findAllByPostsPostId(16L,true);



    }

    @Test
    void inserNewDataToMongo(){
        List<String> imgList = new ArrayList<>();
        for(ImageSources img: imageSourcesRepository.findAllByPostsPostId(16L)){
            imgList.add(img.getSource());
        }
        imgList.add("sssss");
        mongoTestRepositiry.save(MongoTestEntity.builder()
                .id("69364d042f1ced62e3480c11")
                .imgList(imgList)
                .build()
        );



    }

    @Test
    void upsert(){


        Long st = System.currentTimeMillis();
//        System.out.println(mongoTestRepositiry.findByPostId(92000L));

        System.out.println(mongoTestRepositiry.findByContent("testCase41983"));
        System.out.println("nosql 시간"+(System.currentTimeMillis()-st));


    }

}
