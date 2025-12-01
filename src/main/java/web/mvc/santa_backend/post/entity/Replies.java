package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "replies")
public class Replies {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long replyId;

    @Column(nullable = false)
    private  Long userId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts posts;

    @Column(nullable = false)
    private   String replyContent;

    @Column(nullable = false)
    @ColumnDefault("0")
    private  Long replyLike;


}
