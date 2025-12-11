package web.mvc.santa_backend.user.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.user.entity.Users;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponseDTO {
    private Long blockId;
    private Users user;
    private BlockType blockType;
    private Long targetId;
    private LocalDateTime createdAt;
}
