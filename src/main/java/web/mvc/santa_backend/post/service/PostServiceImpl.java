package web.mvc.santa_backend.post.service;

import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.common.S3.S3Uploader;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.dto.PostResponseDTO;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.ImageSources;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.dbtest.RedisPosts;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostResository postRepository;
    @Autowired
    private HashTagsRepository hashTagsRepository;
    @Autowired
    private ImageSourcesRepository imageSourcesRepository;
    @Autowired
    private S3Uploader s3Uploader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RepliesRepository repliesRepository;
    @Autowired
    RedisTemplate<String, RedisPosts> redisTemplate;



    @Transactional
    public Page<PostDTO> getAllPostsWithOffFilter(int pageNo) {


        Pageable pageable = PageRequest.of(pageNo - 1, 5);
//        Page<Posts> page = postRepository.findAllAndContentVisibleTrue(pageable);
        Page<Posts> page = postRepository.findAll(pageable);
        Page<PostDTO> dtoPage = page.map(posts ->
                new PostDTO(
                        posts.getPostId(),
//                        userRepository.findById(posts.getCreateUserId()).get().getUsername(), 실제 사용할떄 사용하는걸로..
                        "TestUser",
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
    public Page<PostResponseDTO> getAllPostsWithOnFilter(Long level, int pageNo) {

        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        Page<Posts> page = postRepository.findAllByPostLevelBetweenAndContentVisibleTrue(0L, level, pageable);

        Page<PostResponseDTO> pageDTO = page.map(posts -> new PostResponseDTO(
                posts.getPostId(),
                userRepository.findById(posts.getCreateUserId()).get().getProfileImage(),
                userRepository.findById(posts.getCreateUserId()).get().getUsername(),
                posts.getCreateAt(),
                posts.getContent(),
                posts.getLikeCount(),
                posts.getPostLevel(),
                posts.isContentVisible(),
                posts.getHashTags().stream().map(hashTags -> hashTags.getTag()).toList(),
                posts.getImageSources().stream().map(imageSources -> imageSources.getSource()).toList(),
                false
        ));
        return pageDTO;
    }

    //map(new::postDTO)로 하려했으나 참조테이블 특정 컬럼 조회해야해서 이게 최선인거같다..
    @Override
    public Page<PostDTO> getFollowPostsWithOffFilter(Long userId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        Page<Posts> page = postRepository.findAllByPostIdAndFollow(userId, pageable);

        Page<PostDTO> pageDTO = page.map(posts -> new PostDTO(
                posts.getPostId(),
                userRepository.findById(posts.getCreateUserId()).get().getUsername(),
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

    @Override
    public Page<PostResponseDTO> getFollowPostsWithOnFilter(Long userId, Long postLevel, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        Page<Posts> page = postRepository.findAllByPostIdAndFollowOnFilter(userId, postLevel, pageable);
        //map(new::postDTO로 하려했으나 참조테이블 특정 컬럼 조회해야해서 이게 최선인거같다..
        Page<PostResponseDTO> pageDTO = page.map(posts -> new PostResponseDTO(
                posts.getPostId(),
                userRepository.findById(posts.getCreateUserId()).get().getProfileImage(),
                userRepository.findById(posts.getCreateUserId()).get().getUsername(),
                posts.getCreateAt(),
                posts.getContent(),
                posts.getLikeCount(),
                posts.getPostLevel(),
                posts.isContentVisible(),
                posts.getHashTags().stream().map(hashTags -> hashTags.getTag()).toList(),
                posts.getImageSources().stream().map(imageSources -> imageSources.getSource()).toList(),
                false
        ));
        return pageDTO;
    }

    @Override
    public Page<PostDTO> getPostsByUserId(Long userId, int pageNo,Long findUser) {

        Pageable pageable = PageRequest.of(pageNo - 1, 5);

        Page<Posts> page;
        if (userId == findUser){
           page = postRepository.findAllByCreateUserIdOrderByCreateAtDesc(userId, pageable);
        }
        else{
         page = postRepository.findAllByCreateUserIdAndContentVisibleIsTrueOrderByCreateAtDesc(userId, pageable);

        }

        Page<PostDTO> pageDTO = page.map(posts -> new PostDTO(
                posts.getPostId(),
                userRepository.findById(posts.getCreateUserId()).get().getUsername(),
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

        Posts savedPost = postRepository.save(Posts.builder().
                createUserId(userRepository.findByUsername(posts.getCreateUserName()).getUserId()).
                createAt(posts.getCreateAt()).
                likeCount(0L).
                content(posts.getContent()).
                postLevel(0L).
                contentVisible(true).
                build()
        );

        List<String> redisImage = new ArrayList<>();
        for(String image : posts.getImageSourcesList()){
            imageSourcesRepository.save(ImageSources.builder()
                            .posts(
                                    savedPost
                            )
                            .source(image)
                    .build());
        redisImage.add(image);

        }
        for(String hash : posts.getHashTagsList()){
            hashTagsRepository.save(
                    HashTags.builder().
                            posts( savedPost)
                                    .tag(hash).
                            build()
            );

        }



        RedisPosts redisPosts = new RedisPosts(savedPost.getPostId(),redisImage, savedPost.getContent());
        redisTemplate.opsForList().rightPush("queue:inference", redisPosts);


    }

    @Transactional
    public void updatePosts(PostDTO posts) {

        //프론트에서 받는 해시태그,이미지는 뺴는 값으로 받음

        Posts post = postRepository.findById(posts.getPostId()).get();
//
//        for (String imgUrl : posts.getImageSourcesList()) {
//
//            imageSourcesRepository.delete(
//                    imageSourcesRepository.findBySource(imgUrl).orElseThrow(() -> new RuntimeException("에러"))
//            );
//
//        }
//        for (String hash : posts.getHashTagsList()) {
//            hashTagsRepository.delete(
//                    hashTagsRepository.findByTagAndPostsPostId(
//                                    hash, posts.getPostId())
//                            .orElseThrow(() ->
//                                    new RuntimeException("에러"))
//
//            );
//        }


        post.setContent(posts.getContent());
        post.setContentVisible(posts.isContentVisible());
        hashTagsRepository.deleteAllByPostsPostId(post.getPostId());
        imageSourcesRepository.deleteAllByPostsPostId(post.getPostId());

        for(String src : posts.getImageSourcesList()){
            imageSourcesRepository.save(ImageSources.builder().
                    posts(post).
                    source(src).

                    build());


        }

        for(String hash : posts.getHashTagsList()){
            hashTagsRepository.save(HashTags.builder().
                    tag(hash).
                    posts(post).
                    build());


        }
    }

    @Transactional
    public void deletePosts(PostDTO posts) {

        repliesRepository.deleteAllByPostsPostId(posts.getPostId());
        postRepository.deleteById(posts.getPostId());
    }


    @Transactional
    public List<String> imgUpload(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        List<ImageSources> imageList = new ArrayList<>();

        System.out.println(files);
        
        for (MultipartFile file : files) {
            System.out.println(file);
            
            try {
                System.out.println("try in");
                urls.add(s3Uploader.uploadFile(file, "test"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

//        for (int i = 0; i < urls.size(); i++) {
//            ImageSources imageSources = imageSourcesRepository.save(
//                    ImageSources.builder().
//                            posts(
//                                    postRepository.findById(postId).get()
//                            ).
//                            source(urls.get(i)).
//                            build()
//            );
//            imageList.add(imageSources);
//        }
//
//        postRepository.findById(postId).get().setImageSources(imageList);
        return urls;
    }

    @Transactional
    public void insertHashTags(String hashTags, Long postId) {
        String[] hashArray = hashTags.split("#");
        Posts post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 입니다"));
        List<HashTags> hashTagList = new ArrayList<>();

        for (int i = 1; i < hashArray.length; i++) {
            HashTags hash = hashTagsRepository.save(HashTags.builder().
                    posts(post).
                    tag("#" + hashArray[i]).
                    build());

            hashTagList.add(hash);
        }
        postRepository.findById(postId).get().setHashTags(hashTagList);

    }

    @Override
    public PostResponseDTO getPostsById(Long postId,Long userId) {

        Posts posts = postRepository.findById(postId).get();

        boolean userCheck =posts.getCreateUserId() == userId;

       PostResponseDTO postResponseDTO = new PostResponseDTO(
                posts.getPostId(),
                userRepository.findById(posts.getCreateUserId()).get().getProfileImage(),
                userRepository.findById(posts.getCreateUserId()).get().getUsername(),
                posts.getCreateAt(),
                posts.getContent(),
                posts.getLikeCount(),
                posts.getPostLevel(),
                posts.isContentVisible(),
                posts.getHashTags().stream().map(hashTags -> hashTags.getTag()).toList(),
                posts.getImageSources().stream().map(imageSources -> imageSources.getSource()).toList(),
                userCheck
        );

        return postResponseDTO;
    }


}

