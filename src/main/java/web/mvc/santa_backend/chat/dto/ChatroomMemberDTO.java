package web.mvc.santa_backend.chat.dto;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.entity.Messages;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatroomMemberDTO {

    private Long chatroomMemberId;
    private Long chatroomId;
    private Long userId;
    private Long startRead;
    private Long lastRead;
    private Boolean noteOff;
    private UserRole role;
    private Boolean isBanned;
}
