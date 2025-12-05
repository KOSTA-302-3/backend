package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.common.S3.S3Uploader;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.ImageSources;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostResository postResository;
    @Autowired
    private HashTagsRepository hashTagsRepository;
    @Autowired
    private ImageSourcesRepository imageSourcesRepository;
    @Autowired
    private S3Uploader s3Uploader;

    @Transactional
    public List<PostDTO> getAllPostsWithOffFilter(int pageNo) {

        List<PostDTO> dtoList = new ArrayList<PostDTO>();

        Pageable pageable = PageRequest.of(pageNo-1, 5);
        Page<Posts> page = postResository.findAll(pageable);

        for (Posts posts : page) {
            dtoList.add(new PostDTO(posts, hashTagsRepository.findAllByPostsPostId(posts.getPostId()),
                    imageSourcesRepository.findAllByPostsPostId(posts.getPostId())

            ));

        }


        return dtoList;
    }

    @Transactional
    public List<PostDTO> getAllPostsWithOnFilter(Long level,int pageNo) {

        List<PostDTO> dtoList = new ArrayList<PostDTO>();

        Pageable pageable = PageRequest.of(pageNo-1, 5);
        Page<Posts> page = postResository.findAllByPostLevelBetween(0L,level,pageable);

        for (Posts posts : page) {
            dtoList.add(new PostDTO(posts, hashTagsRepository.findAllByPostsPostId(posts.getPostId()),
                    imageSourcesRepository.findAllByPostsPostId(posts.getPostId())

            ));

        }


        return dtoList;
    }
    @Transactional
    public void createPosts(PostDTO posts){


        postResository.save(Posts.builder().
                createUserId(posts.getPosts().getPostId()).
                create_at(posts.getPosts().getCreate_at()).
                likeCount(0L).
                content(posts.getPosts().getContent()).
                postLevel(posts.getPosts().getPostLevel()).
                contentVisible(false).
                build()
        );
    }
    @Transactional
    public void updatePosts(PostDTO posts){
        postResository.save(Posts.builder().
                postId(posts.getPosts().getPostId()).
                createUserId(posts.getPosts().getCreateUserId()).
                create_at(posts.getPosts().getCreate_at()).
                content(posts.getPosts().getContent()).
                likeCount(posts.getPosts().getLikeCount()).
                postLevel(posts.getPosts().getPostLevel()).contentVisible(false).
                build()
        );
    }
    @Transactional
    public void deletePosts(PostDTO posts){
        postResository.deleteById(posts.getPosts().getPostId());
    }



    @Transactional
    public void imgUpload(List<MultipartFile> files,Long postId){
        List<String> urls = new ArrayList<>();
        for(MultipartFile file : files){
            try {
                urls.add(s3Uploader.uploadFile(file,"test"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(int i = 0 ; i < urls.size() ; i++){
            imageSourcesRepository.save(
                    ImageSources.builder().
                            posts(
                                    postResository.findById(postId).get()
                            ).
                            source(urls.get(i)).
                            build()
            );


        }



    }

    @Transactional
    public void insertHashTags(String hashTags,Long postId){
        String[] hashArray = hashTags.split("#");

        for(int i = 1 ; i < hashArray.length ;i++) {
            hashTagsRepository.save(HashTags.builder().
                    posts(postResository.findById(postId).get()).
                    tag("#"+hashArray[i]).
                    build());
        }
    }


}

