package web.mvc.santa_backend.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.Posts;

import java.util.List;

@Repository
public interface PostResository extends JpaRepository<Posts, Long> {

    List<Posts> findAllByPostLevelBetween(Long startLevel,Long endLevel);

    @Query(nativeQuery = true,value = "select * from posts limit 2")
    List<Posts> findAllPostsLimit();


    Posts findByContent(String content);

    Page<Posts> findAll(Pageable pageable);




}
