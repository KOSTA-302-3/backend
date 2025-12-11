package web.mvc.santa_backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.common.enumtype.BlockType;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.BlockRequestDTO;
import web.mvc.santa_backend.user.dto.BlockResponseDTO;
import web.mvc.santa_backend.user.dto.FollowDTO;
import web.mvc.santa_backend.user.service.BlockService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
@Slf4j
@Tag(name = "BlockController API", description = "BlockController API (로그인 시에만 가능 = JWT 필요)")
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "차단하기")
    @PostMapping
    public ResponseEntity<?> block(@RequestBody BlockRequestDTO blockRequestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        BlockResponseDTO blockDTO = blockService.block(customUserDetails.getUser().getUserId(), blockRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(blockDTO);
    }

    @Operation(summary = "차단 해제", description = "blocks 테이블에서 레코드 삭제" +
            "(주의) type 입력 시 무조건 대문자로 작성. USER/POST/REPLY")
    @DeleteMapping("/{type}/{targetId}")
    public String unblock(@PathVariable BlockType type, @PathVariable Long targetId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blockService.unblock(customUserDetails.getUser().getUserId(), type, targetId);

        return "차단 해제 완료";
    }

    @Operation(summary = "차단 확인", description = "현재 로그인 한 유저가 다른 유저/게시물을(를) 차단하고 있는지 확인" +
            "(주의) type 입력 시 무조건 대문자로 작성. USER/POST/REPLY")
    @GetMapping("/{type}/{targetId}")
    public ResponseEntity<?> checkBlock(@PathVariable BlockType type, @PathVariable Long targetId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isBlocking = blockService.isBlocking(customUserDetails.getUser().getUserId(), type, targetId);

        return ResponseEntity.status(HttpStatus.OK).body(isBlocking);
    }
}
