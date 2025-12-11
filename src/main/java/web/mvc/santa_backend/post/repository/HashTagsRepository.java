package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.post.entity.HashTags;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashTagsRepository extends JpaRepository<HashTags,Long> {

    List<HashTags> findAllByPostsPostId(Long id);

    Optional<HashTags> findByTag(String tag);

    Optional<HashTags> findByTagAndPostsPostId(String tag, Long id);

    List<TagMapping> findAllByPostsPostId(Long id,boolean hashCk);

}


interface TagMapping {
    Long getTag();
}

