package web.mvc.santa_backend.chat.dto;

import lombok.*;

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
    private boolean online;
}
