package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.user.dto.FollowDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

public interface FollowService {
    /**
     * 팔로우
     * @param followerId : 팔로우 하는 주체 (= user_id)
     * @param followingId : 팔로우 당하는 사람 (= target_id)
     */
    FollowDTO follow(Long followerId, Long followingId);

    /**
     * 언팔로우
     * @param followerId
     * @param followingId
     */
    void unfollow(Long followerId, Long followingId);

    /**
     * 팔로우 상태인지 확인
     */
    boolean isFollowing(Long followerId, Long followingId);

    /**
     * 팔로우 대기 수락
     */
    FollowDTO approveFollow(Long followerId, Long followingId);

    /* 팔로워, 팔로잉 수 증가/감소 */
    void increaseFollowCount(Long followerId, Long followingId);
    void decreaseFollowCount(Long followerId, Long followingId);


    /* 팔로우 조회 관련 */
    List<UserSimpleDTO> getFollowings(Long id);
    Page<UserSimpleDTO> getFollowings(Long id, int page);

    List<UserSimpleDTO> getFollowers(Long id);
    Page<UserSimpleDTO> getFollowers(Long id, int page);

    Page<UserSimpleDTO> getPendingFollowers(Long id, int page);

    /* followCount 수 동기화(TODO 위치 수정 예정) */
    List<UserResponseDTO> updateFollowCounts();

}
