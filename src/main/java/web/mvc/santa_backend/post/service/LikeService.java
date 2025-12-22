package web.mvc.santa_backend.post.service;

public interface LikeService {

    String likeReplies(Long targetId, Long userId);

    String postReplies(Long targetId, Long userId);
}
