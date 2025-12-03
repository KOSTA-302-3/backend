package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.Replies;

import java.util.List;

@Repository
public interface RepliesRepository extends JpaRepository<Replies,Long> {

    List<Replies> findAllByPostsPostId(Long postId);
}
