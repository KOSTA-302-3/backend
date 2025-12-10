package web.mvc.santa_backend.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.CreatedDate;
import web.mvc.santa_backend.common.enumtype.LikeType;
import web.mvc.santa_backend.user.entity.Users;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Liked {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private Users userId;

    @Column(nullable = false)
    private LikeType type;

    @Column(nullable = false)
    private Long targetId;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;



}