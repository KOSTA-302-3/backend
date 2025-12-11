package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.common.exception.*;
import web.mvc.santa_backend.post.entity.Posts;
import web.mvc.santa_backend.post.entity.Replies;
import web.mvc.santa_backend.post.repository.PostResository;
import web.mvc.santa_backend.post.repository.RepliesRepository;
import web.mvc.santa_backend.user.dto.BlockRequestDTO;
import web.mvc.santa_backend.user.dto.BlockResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Blocks;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.BlockRepository;
import web.mvc.santa_backend.user.repository.FollowRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BlockServiceImpl implements BlockService {

    private final UserRepository userRepository;
    private final PostResository postRepository;
    private final RepliesRepository repliesRepository;
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;

    private final FollowService followService;
    private final ModelMapper modelMapper;

    @Override
    public BlockResponseDTO block(Long userId, BlockRequestDTO blockRequestDTO) {
        BlockType type = blockRequestDTO.getBlockType();
        Long targetId = blockRequestDTO.getTargetId();

        // 자기 자신 차단 시
        if (type == BlockType.USER && userId.equals(targetId))
            throw new WrongTargetException(ErrorCode.WRONG_TARGET);

        // id 에 해당하는 유저, 대상(유저/게시물/댓글) 찾기
        Users loginUser = userRepository.findById(userId).orElseThrow(()->new NotFoundException(ErrorCode.USER_NOT_FOUND));
        if ((type == BlockType.USER && userRepository.existsById(targetId) == false) ||
                (type == BlockType.POST && postRepository.existsById(targetId) == false) ||
                (type == BlockType.REPLY && repliesRepository.existsById(targetId) == false))
            throw new NotFoundException(ErrorCode.TARGET_NOT_FOUND);

        // 중복 차단 확인
        if (blockRepository.existsByUser_UserIdAndBlockTypeAndTargetId(userId, type, targetId))
            throw new DuplicateException(ErrorCode.DUPLICATED_BLOCK);

        Blocks block = Blocks.builder()
                .user(loginUser)
                .blockType(type)
                .targetId(targetId)
                .createdAt(LocalDateTime.now())
                .build();
        blockRepository.save(block);
        log.info("user {} blocked {} {}", userId, type, targetId);

        // 유저 차단 시 팔로우 자동 해제
        if (followRepository.existsByFollower_UserIdAndFollowing_UserId(userId, targetId))
            followService.unfollow(userId, targetId);
        if (followRepository.existsByFollower_UserIdAndFollowing_UserId(targetId, userId))
            followService.unfollow(targetId, userId);

        return modelMapper.map(block, BlockResponseDTO.class);
    }

    @Override
    public void unblock(Long userId, BlockType type, Long targetId) {
        Blocks block = blockRepository.findByUser_UserIdAndBlockTypeAndTargetId(userId, type, targetId)
                .orElseThrow(()->new InvalidException(ErrorCode.INVALID_UNBLOCK));

        blockRepository.delete(block);
        log.info("user {} unblocked {} {}", userId, type, targetId);
    }

    @Override
    public boolean isBlocking(Long userId, BlockType type, Long targetId) {
        return blockRepository.existsByUser_UserIdAndBlockTypeAndTargetId(userId, type, targetId);
    }

    /* 차단 조회 */
    @Override
    public List<Object> getBlocks(Long id, BlockType type) {
        List<Blocks> blocks = blockRepository.findByUser_UserIdAndBlockType(id, type);

        /*switch (type) {
            case USER:
                return Collections.singletonList(blocks.stream().map(block -> {
                    Users target = userRepository.findById(block.getTargetId())
                            .orElseThrow(() -> new WrongTargetException(ErrorCode.WRONG_TARGET));
                    return modelMapper.map(target, UserSimpleDTO.class);
                }).toList());
            case POST:
                return Collections.singletonList(blocks.stream().map(block -> {
                    Posts target = postRepository.findById(block.getTargetId())
                            .orElseThrow(() -> new WrongTargetException(ErrorCode.WRONG_TARGET));
                    return modelMapper.map(target, UserSimpleDTO.class);
                }).toList());
            case REPLY:
                return Collections.singletonList(blocks.stream().map(block -> {
                    Replies target = repliesRepository.findById(block.getTargetId())
                            .orElseThrow(() -> new WrongTargetException(ErrorCode.WRONG_TARGET));
                    return modelMapper.map(target, UserSimpleDTO.class);
                }).toList());
            default:
                throw new InvalidException(ErrorCode.INVALID_TYPE);
        }*/
        return null;
    }

    @Transactional
    @Override
    public Page<Object> getBlocks(Long id, BlockType type, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Blocks> blocks = blockRepository.findByUser_UserIdAndBlockType(id, type, pageable);

        // 각 type 에 맞는 DTO 반환
        switch (type) {
            case USER:
                return blocks.map(block -> {
                    Users target = userRepository.findById(block.getTargetId())
                            .orElseThrow(()->new WrongTargetException(ErrorCode.WRONG_TARGET));
                    return modelMapper.map(target, UserSimpleDTO.class);
                });
            case POST:
                return blocks.map(block -> {
                    Posts target = postRepository.findById(block.getTargetId())
                            .orElseThrow(()->new WrongTargetException(ErrorCode.WRONG_TARGET));
                    return modelMapper.map(target, UserSimpleDTO.class);
                });
            case REPLY:
                return blocks.map(block -> {
                    Replies target = repliesRepository.findById(block.getTargetId())
                            .orElseThrow(()->new WrongTargetException(ErrorCode.WRONG_TARGET));
                    return modelMapper.map(target, UserSimpleDTO.class);
                });
            default:
                throw new InvalidException(ErrorCode.INVALID_TYPE);
        }
    }
}
