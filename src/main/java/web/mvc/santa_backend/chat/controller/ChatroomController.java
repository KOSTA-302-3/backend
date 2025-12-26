package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.dto.ChatroomRequestDTO;
import web.mvc.santa_backend.chat.dto.ChatroomResponseDTO;
import web.mvc.santa_backend.chat.service.ChatroomMemberService;
import web.mvc.santa_backend.chat.service.ChatroomService;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.InvalidException;
import web.mvc.santa_backend.common.security.CustomUserDetails;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatroomController API", description = "채팅방 정보 DB저장용")
public class ChatroomController {
    private final ChatroomService chatroomService;
    private final ChatroomMemberService chatroomMemberService;

    @PostMapping("/api/chatroom")
    public ResponseEntity<?> createChatroom(@RequestBody ChatroomRequestDTO chatroomRequestDTO,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        Long chatroomId = chatroomService.createChatroom(chatroomRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatroomId);
    }

    @GetMapping("/api/chatroom")
    public ResponseEntity<Page<ChatroomResponseDTO>> getChatroom(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(required = false) String word,
                                                                 @RequestParam(required = false) String type,
                                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        //로직 전체 수정.
        //프론트에서 userId를 받아오는게 아니라, all, me 정도의 타입을 받아와서 내 채팅방인지 전체 채팅방인지를 조절
        Long userId = null;
        if("me".equals(type)){
            userId = customUserDetails.getUser().getUserId();
        }
        System.out.println(userId);
        page -= 1;
        if(page < 0){
            page = 0;
        }
        return ResponseEntity.status(HttpStatus.OK).body(chatroomService.getChatrooms(userId, word, page));
    }

    @PutMapping("/api/chatroom/")
    public ResponseEntity<?> updateChatroom(@RequestBody ChatroomDTO chatroomDTO,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();

        chatroomService.updateChatroom(chatroomDTO, userId);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @DeleteMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<?> deleteChatroom(@PathVariable Long chatroomId) {
        chatroomService.deleteChatroom(chatroomId);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @PostMapping("/api/chatroom/{userId}")
    public ResponseEntity<?> createDMRoom(@PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long myUserId = customUserDetails.getUser().getUserId();
        Long chatroomId = chatroomService.createChatroom(userId, myUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatroomId);
    }
}
