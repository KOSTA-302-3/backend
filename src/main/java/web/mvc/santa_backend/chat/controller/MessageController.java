package web.mvc.santa_backend.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.ReadUpdateDTO;
import web.mvc.santa_backend.chat.service.MessageService;
import web.mvc.santa_backend.common.S3.S3Uploader;
import web.mvc.santa_backend.common.security.CustomUserDetails;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MessageController API", description = "채팅방 입장시 메시지를 가지고 오는 컨트롤러")
public class MessageController {
    private final MessageService messageService;
    private final S3Uploader s3Uploader;

    /**
     * 기존 참여자가 채팅방에 다시 들어갔을때 메시지를 불러오는 메서드
     * @param chatroomId
     * @return
     */
    @GetMapping("/api/message/{chatroomId}")
    public ResponseEntity<Page<OutboundChatMessageDTO>> getMessages(@PathVariable("chatroomId") Long chatroomId,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @RequestParam(defaultValue = "1") int page) {
        page -= 1;
        if(page < 0){
            page = 0;
        }
        Long userId = customUserDetails.getUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(messageService.getOutboundChatMessages(chatroomId, userId, page));
    }

    /**
     * 테스트용 메서드. 실제로 message저장은 웹소켓 핸들러에서 하게 될 것.
     * @param message
     * @return
     */
    @PostMapping("/api/message")
    public ResponseEntity<?> postMessage(@RequestBody InboundChatMessageDTO message) {
        messageService.createMessage(message);
        return null;
    }

    /**
     * 테스트용 메서드. 실제로 readCount업데이트는 웹소켓 핸들러에서 하게 될 것.
     * @param chatroomId
     * @return
     */
    @GetMapping("/api/message/update/{chatroomId}")
    public ResponseEntity<ReadUpdateDTO> updateReadCount (@PathVariable Long chatroomId,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        messageService.updateFrontUnreadCount(chatroomId, userId);
        return null;
    }

    @GetMapping("/api/message/count")
    public ResponseEntity<?> countUnreadMessages(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(messageService.countAllUnreadMessages(userId));
    }

    @PostMapping("/api/message/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3Uploader.uploadFile(file, "message");
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }
}
