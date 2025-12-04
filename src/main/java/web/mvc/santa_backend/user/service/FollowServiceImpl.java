package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.user.entity.Follows;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.FollowRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Transactional
    @Override
    public void follow(Long followerId, Long followingId) {
        // 자기 자신 팔로우 시
        if (followerId.equals(followingId)) throw new RuntimeException("자신을 팔로우 할 수 없습니다.");

        // id 에 해당하는 유저 찾기
        Users follower = userRepository.findById(followerId).orElseThrow(()->new RuntimeException("Follower not found"));
        Users following = userRepository.findById(followingId).orElseThrow(()->new RuntimeException("Following not found"));

        // 해당 유저가 비활성화(탈퇴) 상태이면 (state=false)
        if (following.isState() == false) throw new RuntimeException("탈퇴한 유저입니다.");
        // 이미 팔로우 한 유저라면 (잘못된 접근)
        if (followRepository.existsByFollower_UserIdAndFollowing_UserId(followerId, followingId))
            throw new RuntimeException("이미 팔로우 중입니다.");

        Follows follow = Follows.builder()
                .follower(follower)
                .following(following)
                .pending(following.isPrivate()) // 상대가 비공개 계정(isPrivate=ture)이면 pending(=true) 처리
                .createdAt(LocalDateTime.now())
                .build();

        // count 증가
        userRepository.increaseFollowingCount(followerId);  // 현재 유저의 팔로잉 수 증가
        userRepository.increaseFollowerCount(followingId);  // 팔로우 당한 유저의 팔로워 수 증가

        followRepository.save(follow);
    }

    @Transactional
    @Override
    public void unfollow(Long followerId, Long followingId) {
        Follows follow = followRepository.findByFollower_UserIdAndFollowing_UserId(followerId, followingId)
                .orElseThrow(()->new RuntimeException("팔로우하지 않는 유저 언팔로우 요청"));

        // count 감소
        userRepository.decreaseFollowingCount(followerId);
        userRepository.decreaseFollowerCount(followingId);

        followRepository.delete(follow);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollower_UserIdAndFollowing_UserId(followerId, followingId);
    }
}
