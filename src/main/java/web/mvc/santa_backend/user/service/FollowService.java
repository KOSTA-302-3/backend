package web.mvc.santa_backend.user.service;

public interface FollowService {
    /**
     * 팔로우
     * @param followerId : 팔로우 하는 주체 (= user_id)
     * @param followingId : 팔로우 당하는 사람 (= target_id)
     */
    public void follow(Long followerId, Long followingId);

    /**
     * 언팔로우
     * @param followerId
     * @param followingId
     */
    public void unfollow(Long followerId, Long followingId);

    /**
     * 팔로우 상태인지 확인
     */
    public boolean isFollowing(Long followerId, Long followingId);
}
