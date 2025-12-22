package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashTags {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private   Long HashTagDetailId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    private  Posts posts;

    @Column(nullable = false)
    private   String tag;


}
