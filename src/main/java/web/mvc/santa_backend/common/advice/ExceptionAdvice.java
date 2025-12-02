package web.mvc.santa_backend.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.ErrorCodeProvider;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException e) {
        ProblemDetail problemDetail;

        if (e instanceof ErrorCodeProvider provider) { // ErrorCodeProvider의 인스턴스라면..
            ErrorCode errorCode = provider.getErrorCode();
            problemDetail = ProblemDetail.forStatus(errorCode.getHttpStatus());
            problemDetail.setTitle(errorCode.getTitle());
            problemDetail.setDetail(errorCode.getMessage());
        } else {//다른 런타임 예외일 경우
            problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            problemDetail.setTitle("Unexpected Error");
            problemDetail.setDetail(e.getMessage());
        }
        problemDetail.setProperty("exception", e.getClass().getSimpleName());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}
