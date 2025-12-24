package web.mvc.santa_backend.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import web.mvc.santa_backend.post.entity.ImageSources;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageSourcesRepository extends JpaRepository<ImageSources,Long> {

    List<ImageSources> findAllByPostsPostId(Long id);

    List<ImageMapping> findAllByPostsPostId(Long id,boolean listck);

    Optional<ImageSources> findBySource(String sources);

    void deleteAllByPostsPostId(Long postId);


}

interface ImageMapping {
    String getSource();
}
