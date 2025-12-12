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
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.common.exception.ChatMemberNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.security.CustomUserDetails;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatroomMemberController API", description = "채팅방 멤버 관리용 테스트 컨트롤러")
public class ChatroomMemberController {
    private final ChatroomMemberService chatroomMemberService;

    @GetMapping("/api/chatmember/{chatroomId}")
    public ResponseEntity<List<UserSimpleDTO>> getChatroomMember(@PathVariable("chatroomId") Long chatroomId, @RequestParam boolean isBanned, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        List<UserSimpleDTO> chatroomMembers = chatroomMemberService.getChatroomMembers(chatroomId, isBanned, userId);

        return ResponseEntity.status(HttpStatus.OK).body(chatroomMembers);
    }

    @PostMapping("/api/chatmember")
    public ResponseEntity<?> addChatroomMember(@RequestBody ChatroomMemberDTO chatroomMemberDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        chatroomMemberDTO.setUserId(userId);
        chatroomMemberService.createChatroomMember(chatroomMemberDTO);
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
        Long userId = customUserDetails.getUser().getUserId();
        chatroomMemberService.deleteChatroomMember(userId, chatroomId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
