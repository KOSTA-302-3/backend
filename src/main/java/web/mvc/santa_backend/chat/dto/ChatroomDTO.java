package web.mvc.santa_backend.chat.dto;


import lombok.*;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomDTO {
    private Long chatroomId;
    private String name;
    private LocalDateTime createdAt;
    @Builder.Default
    private Boolean isPrivate = false;
    private String password;
    @Builder.Default
    private Boolean isDeleted = false;
    private String imageUrl;
    private String description;
    private long countMember;
}
