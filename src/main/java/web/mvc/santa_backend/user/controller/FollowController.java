package web.mvc.santa_backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.user.dto.FollowRequestDTO;
import web.mvc.santa_backend.user.service.FollowService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
@Slf4j
@Tag(name = "FollowController API", description = "FollowController API")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우")
    @PostMapping
    public String follow(@RequestBody FollowRequestDTO followRequestDTO) {
        //Long followerId = getLoginUserId(authentication);
        followService.follow(followRequestDTO.getFollowerId(), followRequestDTO.getFollowingId());

        return "팔로우 완료";
    }

    @Operation(summary = "언팔로우")
    @DeleteMapping("/{followerId}/{followingId}")
    //@DeleteMapping("/{followingId}")  // jwt 적용 시
    public String unfollow(@PathVariable Long followerId, @PathVariable Long followingId) {
        followService.unfollow(followerId, followingId);

        return "언팔로우 완료";
    }

    @Operation(summary = "팔로우 확인")
    @GetMapping("/{followerId}/{followingId}")
    public ResponseEntity<?> checkFollow(@PathVariable Long followerId, @PathVariable Long followingId) {
        boolean isFollowing = followService.isFollowing(followerId, followingId);

        return ResponseEntity.status(HttpStatus.OK).body(isFollowing);
    }
}
