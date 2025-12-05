package web.mvc.santa_backend.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatMemberNotFoundException extends RuntimeException implements ErrorCodeProvider{
    private final ErrorCode errorCode;

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
