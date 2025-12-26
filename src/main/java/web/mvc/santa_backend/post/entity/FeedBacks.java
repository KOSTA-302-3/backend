package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedBacks {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long feedbackId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne
            @JoinColumn(name = "post_id")
    private Posts posts;

    @Column(nullable = false)
    private Long level;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;


}
