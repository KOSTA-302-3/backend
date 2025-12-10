package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.mvc.santa_backend.post.entity.Liked;
import web.mvc.santa_backend.user.entity.Likes;
import web.mvc.santa_backend.user.entity.Users;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {

    Optional<Likes> findByTargetIdAndUser(Long targetId, Users userId);

}
