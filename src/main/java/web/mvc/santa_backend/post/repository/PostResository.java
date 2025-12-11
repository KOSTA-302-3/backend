package web.mvc.santa_backend.post.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.Posts;

import java.util.List;

@Repository
public interface PostResository extends JpaRepository<Posts, Long> {

    Page<Posts> findAllByPostLevelBetween(Long startLevel,Long endLevel,Pageable page);

    @Query(nativeQuery = true,value = "select * from posts limit 2")
    List<Posts> findAllPostsLimit();

    Posts findByContent(String content);

    @Query(nativeQuery = true,value = "select * from posts where create_user_id in (select following_id from follows where follower_id = :user_id and pending = 0)")
    Page<Posts> findAllByPostIdAndFollow(@Param("user_id") Long user_id, Pageable pageable);

    @Query(nativeQuery = true,value = "select * from posts where post_level <= :post_level and create_user_id in (select following_id from follows where follower_id = :user_id and pending = 0)")
    Page<Posts> findAllByPostIdAndFollowOnFilter(@Param("user_id") Long user_id,@Param("post_level") Long post_level, Pageable pageable);

    Page<Posts> findAllByCreateUserId(Long id,Pageable pageable);


     Page<Posts> findAll(Pageable pageable);


}



