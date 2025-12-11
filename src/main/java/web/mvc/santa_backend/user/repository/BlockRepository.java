package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.user.entity.Blocks;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Blocks,Long> {

    /**
     * 차단 해제
     * @param userId : 현재 로그인 한 유저
     * @param blockType : 유저/게시물/댓글
     * @param blockId : 차단 대상의 PK
     */
    Optional<Blocks> findByUser_UserIdAndBlockTypeAndBlockId(Long userId, BlockType blockType, Long blockId);

    /**
     * 차단 중인지 확인
     */
    boolean existsByUser_UserIdAndBlockTypeAndBlockId(Long userId, BlockType blockType, Long blockId);

    /**
     * 로그인 한 유저가 차단한 목록 보기
     * @param id : 현재 로그인 한 유저
     * @param type : 유저/게시물/댓글
     */
    List<Blocks> findByUser_UserIdAndBlockType(Long id, BlockType type);
    Page<Blocks> findByUser_UserIdAndBlockType(Long id, BlockType type, Pageable pageable);

}
