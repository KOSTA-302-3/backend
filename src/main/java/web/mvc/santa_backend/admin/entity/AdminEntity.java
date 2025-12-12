package web.mvc.santa_backend.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import web.mvc.santa_backend.user.entity.Users;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bans")
public class AdminEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "ban_id")
    private Long banId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @Column(nullable = false, length = 255)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String detail;
    
    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(nullable = false, name = "finished_at")
    private LocalDateTime finishedAt;
}
