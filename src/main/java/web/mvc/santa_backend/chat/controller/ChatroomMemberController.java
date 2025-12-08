package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.common.exception.ChatMemberNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatroomMemberController API", description = "채팅방 멤버 관리용 테스트 컨트롤러")
public class ChatroomMemberController {
    private final ChatroomMemberService chatroomMemberService;

    @GetMapping("/api/chatmember/{chatroomId}")
    public ResponseEntity<List<UserSimpleDTO>> getChatroomMember(@PathVariable("chatroomId") Long chatroomId, @RequestParam boolean isBanned) {
        //TODO jwt에서 로그인 중인 userId 찾기
        //임시값
        Long userId = 1L;
        List<UserSimpleDTO> chatroomMembers = chatroomMemberService.getChatroomMembers(chatroomId, isBanned, userId);

        return ResponseEntity.status(HttpStatus.OK).body(chatroomMembers);
    }

    @PostMapping("/api/chatmember")
    public ResponseEntity<?> addChatroomMember(@RequestBody ChatroomMemberDTO chatroomMemberDTO) {
        chatroomMemberService.createChatroomMember(chatroomMemberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/api/chatmember")
    public ResponseEntity<?> updateChatroomMember(@RequestBody ChatroomMemberDTO chatroomMemberDTO) {
        //TODO 현재 로그인하고 있는 사용자의 id를 jwt에서 가지고오기
        //현재 DTO안에는 chatroomId와 상태를 변경할 user의 userId 2개 저장되어있음
        //그리고 추가적으로 변경할 정보를 가지고 있음.
        //임시 userId
        Long userId = 1L;
        chatroomMemberService.updateChatroomMember(userId, chatroomMemberDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/api/chatmember/{userId}/{chatroomId}")
    public ResponseEntity<?> deleteChatroomMember(@PathVariable Long userId, @PathVariable Long chatroomId) {
        chatroomMemberService.deleteChatroomMember(userId, chatroomId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
