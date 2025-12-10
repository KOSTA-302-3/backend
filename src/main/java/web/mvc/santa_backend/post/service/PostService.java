package web.mvc.santa_backend.post.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.post.dto.PostDTO;

public interface PostService {

    Page<PostDTO> getAllPostsWithOffFilter(int pageNo);

    Page<PostDTO> getAllPostsWithOnFilter(Long level, int pageNo);

    void createPosts(PostDTO posts);

    void updatePosts(PostDTO posts);

    void deletePosts(PostDTO posts);

    void insertHashTags(String hashTags, Long postId);


}
