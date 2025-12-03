package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private PostResository postResository;


    public List<RepliesDTO> findReplies(Long id){

        List<RepliesDTO> dtoList = new ArrayList<>();

        for(Replies replies : repliesRepository.findAllByPostsPostId(id)){
            dtoList.add(new RepliesDTO(replies));
        }


        return dtoList;
    };


    public void createReplies(RepliesDTO repliesDTO){
        repliesRepository.save(
                Replies.builder().
                        userId(repliesDTO.getReplies().getUserId()).
                        posts(postResository.findById(repliesDTO.getReplies().getUserId()).get()).
                        replyContent(repliesDTO.getReplies().getReplyContent()).replyLike(repliesDTO.getReplies().getReplyLike()).
                        build()

        );

        System.out.println("댓글 작성 성공!");
    }



   public void updateReplies(RepliesDTO repliesDTO){
        repliesRepository.save(
                Replies.builder().
                        replyId(repliesDTO.getReplies().getReplyId()).
                        userId(repliesDTO.getReplies().getUserId()).
                        posts(postResository.findById(repliesDTO.getReplies().getPosts().getPostId()).get()).
                        replyContent(repliesDTO.getReplies().getReplyContent()).replyLike(repliesDTO.getReplies().getReplyLike()).
                        build()

        );

        System.out.println("댓글 작성 성공!");
    }




  public   void deleteReplies(RepliesDTO repliesDTO){
        repliesRepository.deleteById(repliesDTO.getReplies().getReplyId());
        System.out.println("댓글 삭제 성공!");
    }


}
