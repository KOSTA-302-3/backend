package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Posts {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long postId;
    @Column(nullable = false)
    Long createUserId;

    @Column(nullable = false)
    LocalDateTime create_at;
    @Column(nullable = false)
    Long likeCount;
    @Column(nullable = false)
    Long postLevel;
    @Column(nullable = false)
    boolean contentVisible;

}
