package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HashTags {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long HashTagDetailId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    Posts postId;

    @Column(nullable = false)
    String tag;


}
