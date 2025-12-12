package web.mvc.santa_backend.post.service;

import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import web.mvc.santa_backend.user.repository.UserRepository;

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
    @Autowired
    private UserRepository userRepository;


    @Transactional
    public Page<PostDTO> getAllPostsWithOffFilter(int pageNo) {


        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        Page<Posts> page = postRepository.findAllAndAndContentVisibleTrue(pageable);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        Page<PostDTO> dtoPage = page.map(posts ->
                new PostDTO(
                        posts.getPostId(),
                        userRepository.findById(posts.getCreateUserId()).get().getUsername(),
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
        Page<Posts> page = postRepository.findAllByPostLevelBetweenAndContentVisibleTrue(0L, level, pageable);

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

    //map(new::postDTO)로 하려했으나 참조테이블 특정 컬럼 조회해야해서 이게 최선인거같다..
    @Override
    public Page<PostDTO> getFollowPostsWithOffFilter(Long userId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1,5);
        Page<Posts> page =postRepository.findAllByPostIdAndFollow(userId,pageable);

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
    public Page<PostDTO> getFollowPostsWithOnFilter(Long userId, Long postLevel, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1,5);


        Page<Posts> page =postRepository.findAllByPostIdAndFollowOnFilter(userId,postLevel,pageable);

        //map(new::postDTO로 하려했으나 참조테이블 특정 컬럼 조회해야해서 이게 최선인거같다..
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
    public Page<PostDTO> getPostsByUserId(Long userId, int pageNo) {

        Pageable pageable = PageRequest.of(pageNo-1,5);


        Page<Posts> page =postRepository.findAllByCreateUserId(userId,pageable);

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


        postRepository.save(Posts.builder().
                createUserId(posts.getPostId()).
                createAt(posts.getCreateAt()).
                likeCount(0L).
                content(posts.getContent()).
                postLevel(posts.getPostLevel()).
                contentVisible(true).
                build()
        );
    }

    @Transactional
    public void updatePosts(PostDTO posts) {

        //프론트에서 받는 해시태그,이미지는 뺴는 값으로 받음

        Posts post = postRepository.findById(posts.getPostId()).get();

        for(String imgUrl : posts.getImageSourcesList()){

                imageSourcesRepository.delete(
                        imageSourcesRepository.findBySource(imgUrl).orElseThrow(() -> new RuntimeException("에러"))
                );

        }
        for(String hash : posts.getHashTagsList()){
            hashTagsRepository.delete(
                    hashTagsRepository.findByTagAndPostsPostId(
                            hash,posts.getPostId())
                            .orElseThrow(() ->
                                    new RuntimeException("에러"))

            );
        }


        post.setContent(posts.getContent());
        post.setContentVisible(posts.isContentVisible());
        post.setHashTags(hashTagsRepository.findAllByPostsPostId(posts.getPostId()));
        post.setImageSources(imageSourcesRepository.findAllByPostsPostId(posts.getPostId()));
    }

    @Transactional
    public void deletePosts(PostDTO posts) {
        postRepository.deleteById(posts.getPostId());
    }


    @Transactional
    public void imgUpload(List<MultipartFile> files, Long postId) {
        List<String> urls = new ArrayList<>();
        List<ImageSources> imageList= new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                urls.add(s3Uploader.uploadFile(file, "test"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < urls.size(); i++) {
           ImageSources imageSources =  imageSourcesRepository.save(
                    ImageSources.builder().
                            posts(
                                    postRepository.findById(postId).get()
                            ).
                            source(urls.get(i)).
                            build()
            );
        imageList.add(imageSources);
        }
        postRepository.findById(postId).get().setImageSources(imageList);
    }

    @Transactional
    public void insertHashTags(String hashTags, Long postId) {
        String[] hashArray = hashTags.split("#");
        Posts post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글 입니다"));
        List<HashTags> hashTagList = new ArrayList<>();

        for (int i = 1; i < hashArray.length; i++) {
            HashTags hash =  hashTagsRepository.save(HashTags.builder().
                    posts(post).
                    tag("#" + hashArray[i]).
                    build());

            hashTagList.add(hash);
        }
        postRepository.findById(postId).get().setHashTags(hashTagList);

    }


}

