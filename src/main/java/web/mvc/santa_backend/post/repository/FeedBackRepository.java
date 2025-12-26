package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.FeedBacks;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBacks, Long> {
}
