package web.mvc.santa_backend.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Follows;

@Repository
public interface FollowRepository extends CrudRepository<Follows, Long> {

}
