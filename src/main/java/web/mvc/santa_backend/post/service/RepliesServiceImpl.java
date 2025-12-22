package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;

@Service
public class RepliesServiceImpl implements RepliesService {

    @Autowired
    private RepliesRepository repliesRepository;
    @Autowired
    private PostResository postRepository;

    @Transactional
//    @Cacheable(value = "replies", key = "#id")
    public Page<RepliesDTO> findReplies(Long id, int pageNo) {

        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        System.out.println("접근");
        Page<Replies> page = repliesRepository.findAllByPostsPostId(id, pageable);
        Page<RepliesDTO> pageDTO = page.map(replies -> new RepliesDTO(
                replies.getReplyId(),
                replies.getUserId(),
                replies.getPosts().getPostId(),
                replies.getReplyContent(),
                replies.getReplyLike()
        ));


//        for (Replies replies : repliesRepository.findAllByPostsPostId(id,pageable)) {
//            dtoList.add(new RepliesDTO(replies.getReplyId(),
//                    replies.getUserId(),
//                    replies.getPosts().getPostId(),
//                    replies.getReplyContent(),
//                    replies.getReplyLike()
//            ));
//        }


        return pageDTO;
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
    }


    @Transactional
    public void updateReplies(RepliesDTO repliesDTO) {
       Replies replies =  repliesRepository.findById(repliesDTO.getReplyId()).get();
       replies.setReplyContent(repliesDTO.getReplyContent());
    }

    @Transactional
    public void deleteReplies(RepliesDTO repliesDTO) {
        repliesRepository.deleteById(repliesDTO.getReplyId());
    }


}
