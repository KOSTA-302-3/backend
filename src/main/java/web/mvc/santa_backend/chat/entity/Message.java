package web.mvc.santa_backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import web.mvc.santa_backend.common.enumtype.MessageType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name = "chatroom_id", nullable = false)
    private Long chatroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_member_id", nullable = true)
    private ChatroomMember chatroomMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "reply_message_id")
    private Message replyMessage;

    @Lob
    @Column(nullable = false)
    private String payload;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.TEXT;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long readCount = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long totalMembers = 0L;
}
