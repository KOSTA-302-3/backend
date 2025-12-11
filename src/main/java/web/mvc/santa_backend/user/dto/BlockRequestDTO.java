package web.mvc.santa_backend.user.dto;

import lombok.*;
import web.mvc.santa_backend.common.enumtype.BlockType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDTO {
    private BlockType blockType;
    private Long targetId;
}
