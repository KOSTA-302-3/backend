package web.mvc.santa_backend.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomRequestDTO {
    private String name;
    @Builder.Default
    private Boolean isPrivate = false;
    private String password;
    private String imageUrl;
    private String description;
}
