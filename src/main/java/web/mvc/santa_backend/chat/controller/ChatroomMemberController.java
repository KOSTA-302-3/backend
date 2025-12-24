package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.dto.ChatroomMemberResDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.ChatMemberNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatroomMemberController API", description = "채팅방 멤버 관리용 컨트롤러")
public class ChatroomMemberController {
    private final ChatroomMemberService chatroomMemberService;

    @GetMapping("/api/chatmember/{chatroomId}")
    public ResponseEntity<List<ChatroomMemberResDTO>> getChatroomMember(@PathVariable("chatroomId") Long chatroomId, @RequestParam(defaultValue = "false") boolean isBanned, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        List<ChatroomMemberResDTO> chatroomMembers = chatroomMemberService.getChatroomMembers(chatroomId, isBanned, userId);

        return ResponseEntity.status(HttpStatus.OK).body(chatroomMembers);
    }

    @PostMapping("/api/chatmember")
    public ResponseEntity<?> addChatroomMember(@RequestBody ChatroomMemberDTO chatroomMemberDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("chatmember 등록요청");
        log.info(chatroomMemberDTO.getChatroomId().toString());
        Long userId = customUserDetails.getUser().getUserId();
        chatroomMemberDTO.setUserId(userId);
        ChatroomMembers chatroomMember = chatroomMemberService.createChatroomMember(chatroomMemberDTO);
        //null이라는건 기존에 참여하고 있던 사람이란 뜻. 레코드 생성하지 않았으니 그냥 OK
        if(chatroomMember==null){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/chatmember")
    public ResponseEntity<?> updateChatroomMember(@RequestBody ChatroomMemberDTO chatroomMemberDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        chatroomMemberService.updateChatroomMember(userId, chatroomMemberDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/api/chatmember/{chatroomId}")
    public ResponseEntity<?> deleteChatroomMember(@PathVariable Long chatroomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        //TODO 현재는 채팅방 나가기. 정도의 메서드. 관리자가 차단을 해제하거나 할 때 써야할 메서드를 따로 놓을건지 아니면 이 메서드로 다 쓸건지..
        Long userId = customUserDetails.getUser().getUserId();
        chatroomMemberService.deleteChatroomMember(userId, chatroomId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/api/chatmember/role")
    public ResponseEntity<UserRole> getUserRole(@RequestBody Long chatroomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(chatroomMemberService.getUserRole(chatroomId, userId));
    }
}
