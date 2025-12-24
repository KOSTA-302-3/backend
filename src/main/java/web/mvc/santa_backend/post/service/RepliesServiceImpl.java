package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.NotificationDTO;
import web.mvc.santa_backend.chat.service.NotificationService;
import web.mvc.santa_backend.common.enumtype.NotificationType;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.dto.RepliesReponseDTO;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

@Service
public class RepliesServiceImpl implements RepliesService {

    @Autowired
    private RepliesRepository repliesRepository;
    @Autowired
    private PostResository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    NotificationService notificationService;

    @Transactional
//    @Cacheable(value = "replies", key = "#id")
    public Page<RepliesReponseDTO> findReplies(Long id, int pageNo) {

        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        System.out.println("접근");
        Page<Replies> page = repliesRepository.findAllByPostsPostId(id, pageable);
        Page<RepliesReponseDTO> pageDTO = page.map(replies -> new RepliesReponseDTO(
                replies.getReplyId(),
                userRepository.findById(replies.getUserId()).get().getUsername(),
                replies.getPosts().getPostId(),
                replies.getReplyContent(),
                replies.getReplyLike(),
                userRepository.findById(replies.getUserId()).get().getProfileImage()
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
    public RepliesDTO createReplies(RepliesDTO repliesDTO) {

      Replies replies =  repliesRepository.save(
                Replies.builder().
                        userId(repliesDTO.getUserId()).
                        posts(postRepository.findById(repliesDTO.getPostId()).get()).
                        replyContent(repliesDTO.getReplyContent()).
                        replyLike(0L).
                        build()
        );
      RepliesDTO responseReplies  = new RepliesDTO(
                replies.getReplyId(),
              replies.getUserId(),
              replies.getPosts().getPostId(),
              replies.getReplyContent(),
              replies.getReplyLike()

      );

      if(replies.getPosts().getCreateUserId() != repliesDTO.getUserId()){
          NotificationDTO notificationDTO = new NotificationDTO(0L,replies.getPosts().getCreateUserId(),null,null,false, NotificationType.REPLY,null,repliesDTO.getUserId());
          notificationService.createNotification(notificationDTO);



      }
    return responseReplies;
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
