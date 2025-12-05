package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepliesService {

    @Autowired
    private RepliesRepository repliesRepository;
    @Autowired
    private PostResository postRepository;

    @Transactional
    @Cacheable(value = "replies", key = "#id")
    public List<RepliesDTO> findReplies(Long id) {

        List<RepliesDTO> dtoList = new ArrayList<>();
        System.out.println("접근");
        for (Replies replies : repliesRepository.findAllByPostsPostId(id)) {
            dtoList.add(new RepliesDTO(replies.getReplyId(),
                    replies.getUserId(),
                    replies.getPosts().getPostId(),
                    replies.getReplyContent(),
                    replies.getReplyLike()
            ));
        }


        return dtoList;
    }

    ;

    @Transactional
    public void createReplies(RepliesDTO repliesDTO) {
        System.out.println(repliesDTO.toString());
        repliesRepository.save(
                Replies.builder().
                        userId(repliesDTO.getUserId()).
                        posts(postRepository.findById(repliesDTO.getPostId()).get()).
                        replyContent(repliesDTO.getReplyContent()).
                        replyLike(0L).
                        build()

        );

        System.out.println("댓글 작성 성공!");
    }


    @Transactional
    public void updateReplies(RepliesDTO repliesDTO) {
        repliesRepository.save(
                Replies.builder().
                        replyId(repliesDTO.getReplyId()).
                        userId(repliesDTO.getUserId()).
                        posts(postRepository.findById(repliesDTO.getPostId()).get()).
                        replyContent(repliesDTO.getReplyContent()).
                        replyLike(repliesDTO.getReplyLike()).
                        build()

        );

        System.out.println("댓글 작성 성공!");
    }


    @Transactional
    public void deleteReplies(RepliesDTO repliesDTO) {
        repliesRepository.deleteById(repliesDTO.getReplyId());
        System.out.println("댓글 삭제 성공!");
    }


}
