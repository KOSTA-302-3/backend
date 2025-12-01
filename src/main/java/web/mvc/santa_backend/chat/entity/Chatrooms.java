package web.mvc.santa_backend.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chatrooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomId;

    @Column(length = 100, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isPrivate = false;

    private String password;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @Column(nullable = false, columnDefinition = "varchar(255) default ''")
    private String imageUrl = "";
    private String description;

    @OneToMany(mappedBy = "chatroom", fetch = FetchType.LAZY)
    private List<ChatroomMember> chatroomMembers = new ArrayList<>();
}
