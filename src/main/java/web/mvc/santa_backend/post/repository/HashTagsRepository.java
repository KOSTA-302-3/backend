package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.HashTags;

import java.util.List;

@Repository
public interface HashTagsRepository extends JpaRepository<HashTags,Long> {

    List<HashTags> findAllByPostsPostId(Long id);

    HashTags findByTag(String tag);

}
