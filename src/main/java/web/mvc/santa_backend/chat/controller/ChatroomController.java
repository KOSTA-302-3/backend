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
import web.mvc.santa_backend.chat.service.ChatroomService;
import web.mvc.santa_backend.common.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatroomController API", description = "채팅방 정보 DB저장용")
public class ChatroomController {
    private final ChatroomService chatroomService;

    @PostMapping("/api/chatroom")
    public ResponseEntity<?> createChatroom(@RequestBody ChatroomDTO chatroomDTO) {
        Long chatroomId = chatroomService.createChatroom(chatroomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatroomId);
    }

    @GetMapping("/api/chatroom")
    public ResponseEntity<Page<ChatroomDTO>> getChatroom(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(required = false) String word,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
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

    @PutMapping("api/chatroom/delete/{chatroomId}")
    public ResponseEntity<?> deleteChatroom(@PathVariable Long chatroomId) {
        chatroomService.deleteChatroom(chatroomId);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}
