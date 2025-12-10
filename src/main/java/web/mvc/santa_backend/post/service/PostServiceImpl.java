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
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    private PostResository postRepository;
    @Autowired
    private HashTagsRepository hashTagsRepository;
    @Autowired
    private ImageSourcesRepository imageSourcesRepository;
    @Autowired
    private S3Uploader s3Uploader;

    @Transactional
    public Page<PostDTO> getAllPostsWithOffFilter(int pageNo) {


        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        Page<Posts> page = postRepository.findAll(pageable);

        Page<PostDTO> dtoPage = page.map(posts ->
                new PostDTO(
                        posts.getPostId(),
                        posts.getCreateUserId(),
                        posts.getCreateAt(),
                        posts.getContent(),
                        posts.getLikeCount(),
                        posts.getPostLevel(),
                        posts.isContentVisible(),
                        posts.getHashTags().stream().map(hashTags -> hashTags.getTag()).toList(),
                        posts.getImageSources().stream().map(imageSources -> imageSources.getSource()).toList()
                )
        );


//
//        for (Posts posts : page) {
//            dtoList.add(new PostDTO(posts, hashTagsRepository.findAllByPostsPostId(posts.getPostId()),
//                    imageSourcesRepository.findAllByPostsPostId(posts.getPostId())
//
//            ));
//
//        }


        return dtoPage;
    }

    @Transactional
    public Page<PostDTO> getAllPostsWithOnFilter(Long level, int pageNo) {

        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        Page<Posts> page = postRepository.findAllByPostLevelBetween(0L, level, pageable);

        Page<PostDTO> pageDTO = page.map(posts -> new PostDTO(
                posts.getPostId(),
                posts.getCreateUserId(),
                posts.getCreateAt(),
                posts.getContent(),
                posts.getLikeCount(),
                posts.getPostLevel(),
                posts.isContentVisible(),
                posts.getHashTags().stream().map(hashTags -> hashTags.getTag()).toList(),
                posts.getImageSources().stream().map(imageSources -> imageSources.getSource()).toList()
        ));


        return pageDTO;
    }

    @Transactional
    public void createPosts(PostDTO posts) {


        postRepository.save(Posts.builder().
                createUserId(posts.getPostId()).
                createAt(posts.getCreateAt()).
                likeCount(0L).
                content(posts.getContent()).
                postLevel(posts.getPostLevel()).
                contentVisible(false).
                build()
        );
    }

    @Transactional
    public void updatePosts(PostDTO posts) {
        postRepository.save(Posts.builder().
                postId(posts.getPostId()).
                createUserId(posts.getCreateUserId()).
                createAt(posts.getCreateAt()).
                content(posts.getContent()).
                likeCount(posts.getLikeCount()).
                postLevel(posts.getPostLevel()).contentVisible(false).
                build()
        );
    }

    @Transactional
    public void deletePosts(PostDTO posts) {
        postRepository.deleteById(posts.getPostId());
    }


    @Transactional
    public void imgUpload(List<MultipartFile> files, Long postId) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                urls.add(s3Uploader.uploadFile(file, "test"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < urls.size(); i++) {
            imageSourcesRepository.save(
                    ImageSources.builder().
                            posts(
                                    postRepository.findById(postId).get()
                            ).
                            source(urls.get(i)).
                            build()
            );


        }


    }

    @Transactional
    public void insertHashTags(String hashTags, Long postId) {
        String[] hashArray = hashTags.split("#");
        Posts post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 입니다"));


        for (int i = 1; i < hashArray.length; i++) {
            hashTagsRepository.save(HashTags.builder().
                    posts(post).
                    tag("#" + hashArray[i]).
                    build());
        }
    }


}

