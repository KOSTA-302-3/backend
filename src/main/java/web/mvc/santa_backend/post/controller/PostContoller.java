package web.mvc.santa_backend.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.post.dto.LikeDTO;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.dto.PostResponseDTO;
import web.mvc.santa_backend.post.entity.HashTags;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.service.LikeServiceImpl;
import web.mvc.santa_backend.post.service.PostServiceImpl;
import web.mvc.santa_backend.post.service.RepliesServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostContoller {
    @Autowired
    PostServiceImpl postService;
    @Autowired
    RepliesServiceImpl repliesService;
    @Autowired
    LikeServiceImpl likeService;

    //필터링 끈 전체 게시물 보기
    @ResponseBody
    @GetMapping("/getAllOffFilter")
    @Operation(summary = "필터링 끈 전체 게시물 보기")
    ResponseEntity<Page<PostDTO>> getAllPostsWithOffFilter(int pageNo, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        System.out.println(
                "Call"
        );

        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsWithOffFilter(pageNo));
    }

    //필터링 킨 전체 게시물 보기
    @ResponseBody
    @GetMapping("/getAllOnFilter")
    @Operation(summary = "필터링 킨 전체 게시물 보기")
    ResponseEntity<Page<PostResponseDTO>> getAllPostsWithOnFilter(Long postLevel, int pageNo, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsWithOnFilter(postLevel, pageNo));

    }

    //필터링 끈 팔로우 게시물 보기
    @Operation(summary = "필터링 끈 팔로우 게시물 보기")
    @GetMapping("/getFollowOffFilter")
    ResponseEntity<Page<PostDTO>> getFollowPostsWithOffFilter(@RequestParam Long userId, @RequestParam int pageNo, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(postService.getFollowPostsWithOffFilter(userId, pageNo));
    }

    //필터링 킨 팔로우 게시물 보기
    @Operation(summary = "필터링 킨 팔로우 게시물 보기")
    @GetMapping("/getFollowOnFilter")
    ResponseEntity<Page<PostResponseDTO>> getFollowPostsWithOnFilter(@RequestParam Long postLevel, @RequestParam int pageNo,@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        long userId = customUserDetails.getUser().getUserId();

        return ResponseEntity.status(HttpStatus.OK).body(postService.getFollowPostsWithOnFilter(userId, postLevel, pageNo));
    }

    @Operation(summary = "특정 유저 게시물 보기")
    @GetMapping("/getPostsByUserId")
    ResponseEntity<Page<PostDTO>> getPostsByUserId(@RequestParam Long userId, @RequestParam int pageNo,@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByUserId(userId, pageNo));
    }


    @Operation(summary = "특정 게시물 보기")
    @GetMapping("/getPostsByPostId")
    ResponseEntity<PostResponseDTO> getPostsByPostId(@RequestParam Long PostId,@AuthenticationPrincipal CustomUserDetails customUserDetails) {


        Long userId = customUserDetails.getUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsById(PostId,userId));
    }



    //게시물 작성
    @PostMapping(value = "/createPosts")
    @Operation(summary = "게시물 작성")
    ResponseEntity<String> createPosts(@RequestBody PostDTO postDTO,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails.getAuthorities() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        postDTO.setCreateUserName(customUserDetails.getUser().getUsername());
        postService.createPosts(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Create Success");
    }

    //게시물 수정
    @PutMapping("/updatePosts")
    @Operation(summary = "게시물 수정")
    ResponseEntity<String> updatePosts(@RequestBody PostDTO postDTO) {
        postService.updatePosts(postDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Update Success");

    }

    //게시물 삭제
    @DeleteMapping("/deletePosts")
    @Operation(summary = "게시물 삭제")
    ResponseEntity<String> deletePosts(@RequestBody PostDTO postDTO,@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println(postDTO.toString());
        postService.deletePosts(postDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete Success");

    }

    @PostMapping(value = "/imageUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 작성")
    ResponseEntity<List<String>> createimage(@RequestPart List<MultipartFile> files ,@AuthenticationPrincipal CustomUserDetails customUserDetails) {


        return ResponseEntity.status(HttpStatus.CREATED).body(postService.imgUpload(files));
    }

    @PostMapping(value = "/hashTagsInsert/{postId}")
    @Operation(summary = "해시태그 작성")
    ResponseEntity<String> insertHashTag(String hashTags, @PathVariable Long postId) {


        postService.insertHashTags(hashTags, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body("해그태그 작성완료");
    }

    @PostMapping("/like")
    @Operation(summary = "게시물 좋아요")
    ResponseEntity<String> likePost(@RequestBody LikeDTO likeDTO,@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        likeDTO.setUserId(customUserDetails.getUser().getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(
                likeService.postReplies(likeDTO.getTargetId(), likeDTO.getUserId())
        );
    }

    @GetMapping("/ckLike")
    @Operation(summary = "좋아요 여부 확인")
    ResponseEntity<Boolean> likePost(@RequestParam Long targerId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        LikeDTO likeDTO = new LikeDTO(targerId,customUserDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(
                likeService.ckLike(likeDTO)
        );
    }
    

    @GetMapping("testCode")
    public String testA(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("Call!");
        System.out.println(customUserDetails.getUsername());
        return customUserDetails.getUsername();
    }


}
