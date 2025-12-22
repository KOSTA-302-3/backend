package web.mvc.santa_backend.post.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.dto.PostResponseDTO;
import web.mvc.santa_backend.post.entity.Posts;

import java.util.List;

public interface PostService {

    Page<PostDTO> getAllPostsWithOffFilter(int pageNo);

    Page<PostResponseDTO> getAllPostsWithOnFilter(Long level, int pageNo);

    Page<PostDTO> getFollowPostsWithOffFilter(Long userId, int pageNo);
    Page<PostResponseDTO> getFollowPostsWithOnFilter(Long userId,Long postLevel,int pageNo);

    Page<PostDTO> getPostsByUserId(Long userId,int pageNo);

    void createPosts(PostDTO posts);

    void updatePosts(PostDTO posts);

    void deletePosts(PostDTO posts);

    void insertHashTags(String hashTags, Long postId);

    PostResponseDTO getPostsById(Long postId);

}
