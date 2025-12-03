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
    DUPLICATED(HttpStatus.BAD_REQUEST, "Duplicated id", "아이디 중복");

    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
