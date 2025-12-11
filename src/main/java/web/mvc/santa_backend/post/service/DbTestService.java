package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.entity.dbtest.MongoTestEntity;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.dbtest.MongoTestRepositiry;

import java.util.Optional;

@Service

public class DbTestService {
    @Autowired
    PostResository postResository;
    @Autowired
    ImageSourcesRepository imageSourcesRepository;
    @Autowired
    HashTagsRepository hashTagsRepository;
    @Autowired
    MongoTestRepositiry mongoTestRepositiry;

    //redis 강의 참고
    //다만 redis key는 Ano 에서는value key는 메서드에서 쓰는 파라미터
    //ex) post:postId 이렇게 들어감
    //레디스에 같은 데이터가 있으면 db접근 안하고 없으면한다 출력문을 확인해보자
    @Cacheable(value = "post" , key = "#postId")
    public PostDTO getPostsByid(Long postId){
        Pageable pageable = PageRequest.of(0,5);


        System.out.println("접근");

        Optional<PostDTO> post = postResository.findById(postId).map(posts -> new PostDTO(
                posts.getPostId(),
                posts.getCreateUserId(),
                posts.getCreateAt(),
                posts.getContent(),
                posts.getPostLevel(),
                posts.getLikeCount(),
                posts.isContentVisible(),
                posts.getHashTags().stream().map(hash -> hash.getTag()).toList(),
                posts.getImageSources().stream().map(img -> img.getSource()).toList()

        ));

        return post.get();
    };

    public Page<MongoTestEntity> mongoTest(){

        Pageable pageable = PageRequest.of(0,10);

        return mongoTestRepositiry.findAll(pageable);


    }

}
