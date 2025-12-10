package web.mvc.santa_backend.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.post.dto.LikeDTO;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.service.LikeServiceImpl;
import web.mvc.santa_backend.post.service.PostServiceImpl;
import web.mvc.santa_backend.post.service.RepliesServiceImpl;

@RestController
@RequestMapping("/replies")
public class RepliesController {
    @Autowired
    PostServiceImpl postService;
    @Autowired
    RepliesServiceImpl repliesService;
    @Autowired
    LikeServiceImpl likeService;

    @ResponseBody
    @GetMapping("/getReplies")
    @Operation(summary = "게시물 댓글보기")
    ResponseEntity<Page<RepliesDTO>> getReplies(@RequestParam Long id, @RequestParam int pageNo){

        long st = System.currentTimeMillis();

        Page<RepliesDTO> replies= repliesService.findReplies(id,pageNo);

        System.out.println(System.currentTimeMillis()-st);
        return ResponseEntity.status(HttpStatus.CREATED).body(replies);

    }
    //댓글쓰기
    @PostMapping("/createReplies")
    @Operation(summary = "댓글쓰기")
    ResponseEntity<String> createReplies(@RequestBody RepliesDTO repliesDTO){
        repliesService.createReplies(repliesDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("Create Success");

    }

    //댓글 수정
    @PutMapping("/updateReplies")
    @Operation(summary = "댓글 수정")
    ResponseEntity<String> updateReplies(@RequestBody RepliesDTO repliesDTO){
        repliesService.updateReplies(repliesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Update Success");

    }

    //댓글 삭제
    @DeleteMapping("/deleteReplies")
    @Operation(summary = "댓글 삭제")
    ResponseEntity<String> deleteReplies(@RequestBody RepliesDTO repliesDTO){
        repliesService.deleteReplies(repliesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Delete Success");
    }

    @PostMapping("/like")
    @Operation(summary = "필터링 킨 전체 게시물 보기")
    ResponseEntity<String> likeReplies(@RequestBody LikeDTO likeDTO){




        return ResponseEntity.status(HttpStatus.CREATED).body(
                likeService.likeReplies(likeDTO.getTargetId(),likeDTO.getUserId())
        );
    }







}

