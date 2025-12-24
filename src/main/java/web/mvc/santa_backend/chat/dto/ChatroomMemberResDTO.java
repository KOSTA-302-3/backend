package web.mvc.santa_backend.chat.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.UserRole;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatroomMemberResDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private UserRole role;
    private boolean online;
}
