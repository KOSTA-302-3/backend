package web.mvc.santa_backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.FollowDTO;
import web.mvc.santa_backend.user.dto.FollowRequestDTO;
import web.mvc.santa_backend.user.service.FollowService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
@Slf4j
@Tag(name = "FollowController API", description = "FollowController API (로그인 시에만 가능 = JWT 필요)")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우")
    @PostMapping
    public ResponseEntity<?> follow(@RequestBody FollowRequestDTO followDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        FollowDTO follow = followService.follow(customUserDetails.getUser().getUserId(), followDTO.getFollowingId());

        return ResponseEntity.status(HttpStatus.CREATED).body(follow);
    }

    @Operation(summary = "언팔로우 및 팔로우 거절", description = "언팔로우 / 비공개 유저에게 온 팔로우 요청을 거절 (둘 다 레코드 삭제)")
    @DeleteMapping("/{followingId}")
    public String unfollow(@PathVariable Long followingId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        followService.unfollow(customUserDetails.getUser().getUserId(), followingId);

        return "언팔로우 완료";
    }

    @Operation(summary = "팔로우 확인", description = "현재 로그인 한 유저가 다른 유저를 팔로우 하고 있는지 확인")
    @GetMapping("/{followingId}")
    public ResponseEntity<?> checkFollow(@PathVariable Long followingId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isFollowing = followService.isFollowing(customUserDetails.getUser().getUserId(), followingId);

        return ResponseEntity.status(HttpStatus.OK).body(isFollowing);
    }

    @Operation(summary = "팔로우 수락", description = "비공개 유저에게 온 팔로우 요청을 수락")
    @PutMapping("/{followerId}")
    public ResponseEntity<?> approveFollow(@PathVariable Long followerId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        FollowDTO follow = followService.approveFollow(followerId, customUserDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.OK).body(follow);
    }

}
