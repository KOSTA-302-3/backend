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
    private    Long imageId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    private  Posts posts;

    @Column(nullable = false,length = 255)
    private  String source;

}
