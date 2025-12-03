package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.repository.HashTagsRepository;
import web.mvc.santa_backend.post.repository.ImageSourcesRepository;
import web.mvc.santa_backend.post.repository.PostResository;

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

    public List<PostDTO> getAllPostsWithOffFilter() {

        List<PostDTO> dtoList = new ArrayList<PostDTO>();

        List<Posts> postList = postResository.findAll();

        for (Posts posts : postList) {
            dtoList.add(new PostDTO(posts, hashTagsRepository.findAllByPostsPostId(posts.getPostId()),
                    imageSourcesRepository.findAllByPostsPostId(posts.getPostId())

            ));

        }


        return dtoList;
    }


    public List<PostDTO> getAllPostsWithOnFilter(Long level) {

        List<PostDTO> dtoList = new ArrayList<PostDTO>();

        List<Posts> postList = postResository.findAllByPostLevel(level);

        for (Posts posts : postList) {
            dtoList.add(new PostDTO(posts, hashTagsRepository.findAllByPostsPostId(posts.getPostId()),
                    imageSourcesRepository.findAllByPostsPostId(posts.getPostId())

            ));

        }


        return dtoList;
    }

    public void createPosts(PostDTO posts){
        postResository.save(Posts.builder().
                createUserId(posts.getPosts().getPostId()).
                create_at(posts.getPosts().getCreate_at()
                ).likeCount(posts.getPosts().getLikeCount()).postLevel(posts.getPosts().getPostLevel()).contentVisible(false).
                build()
        );
    }

    public void updatePosts(PostDTO posts){
        postResository.save(Posts.builder().
                postId(posts.getPosts().getPostId()).
                createUserId(posts.getPosts().getCreateUserId()).
                create_at(posts.getPosts().getCreate_at()
                ).likeCount(posts.getPosts().getLikeCount()).postLevel(posts.getPosts().getPostLevel()).contentVisible(false).
                build()
        );
    }

    public void deletePosts(PostDTO posts){
        postResository.deleteById(posts.getPosts().getPostId());
    }


}
