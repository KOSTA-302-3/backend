package web.mvc.santa_backend.user.entity;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.santa_backend.common.enumtype.BlockType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Blocks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "block_id")
    private Long blockId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockType blockType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false, name = "created_at", columnDefinition = "datetime default now()")
    private LocalDateTime createdAt;
}
