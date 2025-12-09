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
@RequestMapping("/api/follow")
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

    @Operation(summary = "언팔로우 및 팔로우 거절", description = "언팔로우 / 비공개 유저에게 온 팔로우 요청을 거절 (둘 다 레코드 삭제)")
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

    @Operation(summary = "팔로우 수락", description = "비공개 유저에게 온 팔로우 요청을 수락")
    @PutMapping("/{followerId}/{followingId}")
    public String approveFollow(@PathVariable Long followerId, @PathVariable Long followingId) {
        followService.approveFollow(followerId, followingId);

        return "팔로우 수락";
    }

}
