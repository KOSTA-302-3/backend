package web.mvc.santa_backend.chat.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomResponseDTO {
    private Long id;
    private String name;
    private boolean isPrivate;
    private Long membersCount;
    private boolean hasUnread;
    private String imageUrl;
}
