package web.mvc.santa_backend.common.exception;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class InvalidException extends RuntimeException implements ErrorCodeProvider {
    private final ErrorCode errorCode;

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
