package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImageSources {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long imageId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    Posts posts;

    @Column(nullable = false,length = 255)
    String source;

}
