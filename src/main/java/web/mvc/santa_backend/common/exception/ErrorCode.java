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
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "duplicated username", "아이디 중복"),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "duplicated email", "이메일 중복"),
    DUPLICATED_BLOCK(HttpStatus.BAD_REQUEST, "duplicated block", "중복 차단 불가"),
    DUPLICATED_CHAT_MEMBER(HttpStatus.BAD_REQUEST, "Duplicated chatroom member", "중복입장 불가능"),

    WRONG_TARGET(HttpStatus.BAD_REQUEST, "wrong target", "자기 자신에게 요청할 수 없음"),

    INVALID_FOLLOW(HttpStatus.BAD_REQUEST, "invalid follow", "팔로우 불가"),
    INVALID_UNFOLLOW(HttpStatus.BAD_REQUEST, "invalid unfollow", "팔로우하지 않은 유저 언팔로우 불가"),
    INVALID_UNBLOCK(HttpStatus.BAD_REQUEST, "invalid unblock", "차단하지 않은 유저 차단 해제 불가"),
    INVALID_APPROVE(HttpStatus.BAD_REQUEST, "invalid approve", "이미 수락한 유저"),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "invalid type", "유효하지 않은 타입"),

    CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "chatroom not found", "채팅방이 없음"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "user not found", "유저가 존재하지 않음"),
    USER_DEACTIVATED(HttpStatus.BAD_REQUEST, "user not found(deactivated)", "탈퇴한 유저"),
    TARGET_NOT_FOUND(HttpStatus.BAD_REQUEST, "target not found", "해당 대상이 존재하지 않음"),
    CHATMEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "chatmember not found", "채팅멤버가 없음"),

    NOT_CHATMEMBER(HttpStatus.BAD_REQUEST, "not chatmember", "채팅멤버가 아님"),
    NOT_CHATROOM_ADMIN(HttpStatus.FORBIDDEN, "not chatroom admin", "채팅방 관리자가 아님");
    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
