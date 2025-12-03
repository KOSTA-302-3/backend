package web.mvc.santa_backend.post.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.dto.RepliesDTO;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.service.PostService;
import web.mvc.santa_backend.post.service.RepliesService;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostContoller {
    @Autowired
    PostService postService;
    @Autowired
    RepliesService repliesService;

    //필터링 끈 전체 게시물 보기
    @ResponseBody
    @GetMapping("/getAllOffFilter")
    List<PostDTO> getAllPostsWithOffFilter() {
        return  postService.getAllPostsWithOffFilter();
    }
    //필터링 킨 전체 게시물 보기
    @ResponseBody
    @GetMapping("/getAllOnFilter")
    List<PostDTO> getAllPostsWithOnFilter(Long level) {

        return postService.getAllPostsWithOnFilter(level);
    }

    //필터링 끈 팔로우 게시물 보기
    @GetMapping("/getFollowOffFilter")
    List<Posts> getFollowPostsWithOffFilter() {

        return null;
    }
    //필터링 킨 팔로우 게시물 보기
    @GetMapping("/getFollowOnFilter")
    List<Posts> getFollowPostsWithOnFilter() {

        return null;
    }

    //게시물 작성
    @PostMapping("/createPosts")
    Posts createPosts(@RequestBody PostDTO postDTO){
        postService.createPosts(postDTO);
        return null;

    }

    //게시물 수정
    @PutMapping("/updatePosts")
    Posts updatePosts(@RequestBody PostDTO postDTO){
        postService.updatePosts(postDTO);
        return null;

    }

    //게시물 삭제
    @DeleteMapping("/deletePosts")
    Posts deletePosts(@RequestBody PostDTO postDTO){
        postService.deletePosts(postDTO);
        return null;

    }

    //댓글보기
    @ResponseBody
    @GetMapping("/getReplies")
    List<RepliesDTO> getReplies(@RequestParam Long id){

        return repliesService.findReplies(id);

    }
    //댓글쓰기
    @PostMapping("/createReplies")
    Posts createReplies(@RequestParam RepliesDTO repliesDTO){
        repliesService.createReplies(repliesDTO);
        return null;

    }

    //댓글 수정
    @PutMapping("/updateReplies")
    Posts updateReplies(@RequestParam RepliesDTO repliesDTO){
        repliesService.updateReplies(repliesDTO);
        return null;

    }

    //댓글 삭제
    @DeleteMapping("/deleteReplies")
    Posts deleteReplies(@RequestParam RepliesDTO repliesDTO){
        repliesService.deleteReplies(repliesDTO);
        return null;

    }





}
