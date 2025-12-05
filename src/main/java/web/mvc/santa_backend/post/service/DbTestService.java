package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.entity.ImageSources;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.repository.EntityRepository;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;

@Service

public class DbTestService {
    @Autowired
    PostResository postResository;
    ImageSourcesRepository imageSourcesRepository;

    HashTagsRepository hashTagsRepository;


    //redis 강의 참고
    //다만 redis key는 Ano 에서는value key는 메서드에서 쓰는 파라미터
    //ex) post:postId 이렇게 들어감
    //레디스에 같은 데이터가 있으면 db접근 안하고 없으면한다 출력문을 확인해보자
    @Cacheable(value = "post" , key = "#postId")
    public PostDTO getPostsByid(Long postId){
        System.out.println("접근");
        return new PostDTO(
                postResository.findById(postId).get()
                ,hashTagsRepository.findAllByPostsPostId(postId)
                ,imageSourcesRepository.findAllByPostsPostId(postId)
        );
    };

}
