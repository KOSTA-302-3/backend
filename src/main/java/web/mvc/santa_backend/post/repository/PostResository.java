package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.Posts;

import java.util.List;

@Repository
public interface PostResository extends JpaRepository<Posts, Long> {

    List<Posts> findAllByPostLevel(Long level);



}
