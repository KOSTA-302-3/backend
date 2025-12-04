package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.service.ChatroomService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ChatController API", description = "채팅관련 정보 DB저장용")
public class ChatroomContoller {
    private final ChatroomService chatroomService;

    @PostMapping("/api/chatroom")
    public ResponseEntity<?> createChatroom(@RequestBody ChatroomDTO chatroomDTO) {
        chatroomService.createChatroom(chatroomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    @GetMapping("/api/chatroom")
    public ResponseEntity<Page<ChatroomDTO>> getChatroom(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(required = false) String word,
                                                         @RequestParam(required = false) Long userId) {
        //TODO
        //들어오는 userId와 jwt에 있는 userId의 값을 비교해서 인증처리해야함
        page -= 1;
        if(page < 0){
            page = 0;
        }
        return ResponseEntity.status(HttpStatus.OK).body(chatroomService.getChatrooms(userId, word, page));
    }

    @PutMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<?> updateChatroom(@PathVariable Long chatroomId, @RequestBody ChatroomDTO chatroomDTO) {
        //TODO
        //업데이트 요청을 하는 사람은 ChatroomMember의 role.ADMIN 한정..
        //그렇다면..ChatroomMember에서 userId와 roomId로 조회해서 레코드 하나를 찾고.. 그 레코드 안에서 role을 확인하는 로직이 필요

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}
