package web.mvc.santa_backend.post.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.post.dto.RepliesDTO;

public interface RepliesService {
    Page<RepliesDTO> findReplies(Long id, int pageNo);

    RepliesDTO createReplies(RepliesDTO repliesDTO);

    void updateReplies(RepliesDTO repliesDTO);

    void deleteReplies(RepliesDTO repliesDTO);



}
