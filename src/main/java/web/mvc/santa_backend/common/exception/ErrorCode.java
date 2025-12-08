package web.mvc.santa_backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 필요한 에러코드 추가할 것..
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATED(HttpStatus.BAD_REQUEST, "Duplicated id", "아이디 중복"),
    DUPLICATED_CHAT_MEMBER(HttpStatus.BAD_REQUEST, "Duplicated chatroom member", "중복입장 불가능"),
    CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "chatroom not found", "채팅방이 없음"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "user not found", "유저가 존재하지 않음"),
    CHATMEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "chatmember not found", "채팅멤버가 없음"),
    NOT_CHATMEMBER(HttpStatus.BAD_REQUEST, "not chatmember", "채팅멤버가 아님"),
    NOT_CHATROOM_ADMIN(HttpStatus.FORBIDDEN, "not chatroom admin", "채팅방 관리자가 아님");
    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
