package web.mvc.santa_backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "CHATROOM_USER",
                        columnNames = {"chatroom_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomMemeberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id", nullable = false)
    private Chatrooms chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private Users user;

    private Long startRead;
    private Long lastRead;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean noteOff = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isBanned = false;

    @OneToMany(mappedBy = "chatroomMember", fetch = FetchType.LAZY)
    private List<Messages> messages;

}
