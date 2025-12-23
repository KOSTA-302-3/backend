package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "replies")
public class Replies {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long replyId;

    @Column(nullable = false)
    private  Long userId;

    @ManyToOne()
    @JoinColumn(name = "post_id")
    private Posts posts;

    @Column(nullable = false)
    private   String replyContent;

    @Column(nullable = false)
    @ColumnDefault("0")
    private  Long replyLike;


   public void increaseLikeCount(){
        this.replyLike++;
    }

    public void decreaseLikeCount(){
        this.replyLike--;
    }

}
