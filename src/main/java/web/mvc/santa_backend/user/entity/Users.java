package web.mvc.santa_backend.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import web.mvc.santa_backend.common.enumtype.UserRole;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean state = true;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long point = 0L;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPrivate = false;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long followingCount = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long followerCount = 0L;

    @Column(nullable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne
    @MapsId
    private Customs custom;
}
