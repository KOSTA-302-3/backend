package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.user.dto.BlockRequestDTO;
import web.mvc.santa_backend.user.dto.BlockResponseDTO;

import java.util.List;

public interface BlockService {
    /**
     * 차단
     */
    BlockResponseDTO block(Long userId, BlockRequestDTO blockRequestDTO);

    /**
     * 차단 해제
     */
    void unblock(Long userId, BlockType type, Long targetId);

    /**
     * 차단 확인
     */
    boolean isBlocking(Long userId, BlockType type, Long targetId);

    /* 차단 조회 */
    List<Object> getBlocks(Long id, BlockType type);
    Page<Object> getBlocks(Long id, BlockType type, int page);
}
