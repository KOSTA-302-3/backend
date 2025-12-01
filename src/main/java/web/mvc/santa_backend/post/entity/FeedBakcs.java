package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FeedBakcs {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long feedbakcId;

    @Column(nullable = false)
    Long userId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    Posts posts;

    @Column(nullable = false)
    Long level;

    @Column(nullable = false)
    LocalDateTime createAt;

}
