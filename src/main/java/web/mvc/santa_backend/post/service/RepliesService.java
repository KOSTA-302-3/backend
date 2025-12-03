package web.mvc.santa_backend.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.RepliesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepliesService {

    @Autowired
    private RepliesRepository repliesRepository;


    public List<RepliesDTO> findReplies(Long id){

        List<RepliesDTO> dtoList = new ArrayList<>();

        for(Replies replies : repliesRepository.findAllByPostsPostId(id)){
            dtoList.add(new RepliesDTO(replies));
        }


        return dtoList;
    };

}
