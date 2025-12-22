package web.mvc.santa_backend.post.repository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.Posts;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface PostResository extends JpaRepository<Posts, Long> {
    

    // 통계용: 오늘 올라온 게시글 수
    @Query("SELECT COUNT(p) FROM Posts p WHERE p.createAt >= :startOfDay")
    long countByCreateAtAfter(LocalDateTime startOfDay);

    @Query(nativeQuery = true,value = "select * from posts where content_visible = 1 and post_level between :startLevel and :endLevel and create_user_id in (select user_id from users where is_private = 0) order by post_id desc")
    Page<Posts> findAllByPostLevelBetweenAndContentVisibleTrue(Long startLevel,Long endLevel,Pageable page);
    //배포시에는 위에 테스트 시에는 밑에
    //@Query(nativeQuery = true,value = "select * from posts where content_visible = 1 and create_user_id in (select user_id from users where is_private = 0)")
    @Query(nativeQuery = true,value = "select * from posts where content_visible = 1")
    Page<Posts> findAllAndContentVisibleTrue(Pageable page);
    @Query(nativeQuery = true,value = "select * from posts where content_visible = 1 and create_user_id in (select following_id from follows where follower_id = :user_id and pending = 0)")
    Page<Posts> findAllByPostIdAndFollow(@Param("user_id") Long user_id, Pageable pageable);
    @Query(nativeQuery = true,value = "select * from posts where content_visible = 1 and post_level <= :post_level and create_user_id in (select following_id from follows where follower_id = :user_id and pending = 0)")
    Page<Posts> findAllByPostIdAndFollowOnFilter(@Param("user_id") Long user_id,@Param("post_level") Long post_level, Pageable pageable);
    Page<Posts> findAllByCreateUserId(Long id,Pageable pageable);
    Page<Posts> findAll(Pageable pageable);


    }



