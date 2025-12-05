package web.mvc.santa_backend.post.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.post.dto.PostDTO;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.service.DbTestService;

@RestController
@RequestMapping("/test")
public class DbTestController {
    @Autowired
    DbTestService dbTestService;

    @GetMapping("/getPosts")
    @ResponseBody
    PostDTO redisTest(@RequestParam Long postId){
        long st = System.currentTimeMillis();
        PostDTO post = dbTestService.getPostsByid(postId);
        System.out.println(post);
        System.out.println(System.currentTimeMillis()-st);
        return post;
    }



}
