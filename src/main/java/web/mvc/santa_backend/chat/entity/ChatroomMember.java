package web.mvc.santa_backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.santa_backend.common.enumtype.UserRole;

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
public class ChatroomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomMemeberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id", nullable = false)
    private Chatroom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private Long startRead;
    private Long lastRead;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean noteOff = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isBanned = false;

    @OneToMany(mappedBy = "chatroomMember", fetch = FetchType.LAZY)
    private List<Message> messages;

}
