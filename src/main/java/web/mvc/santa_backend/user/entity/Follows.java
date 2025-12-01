package web.mvc.santa_backend.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "follow_id")
    private Long followId;

    @ManyToOne
    @JoinColumn(nullable = false, name = "follower_id")
    private Users follower;

    @ManyToOne
    @JoinColumn(nullable = false, name = "following_id")
    private Users following;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean pending = false;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
