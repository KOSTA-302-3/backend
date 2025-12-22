package web.mvc.santa_backend.post.service;

import web.mvc.santa_backend.post.dto.LikeDTO;

public interface LikeService {

    String likeReplies(Long targetId, Long userId);

    String postReplies(Long targetId, Long userId);

    boolean ckLike(LikeDTO likeDTO);
}
