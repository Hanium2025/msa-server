package hanium.apigateway_service.exception;

import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * GlobalExceptionHandler는 애플리케이션 전역에서 발생하는 예외를
 * 처리하는 컨트롤러 조언(Controller Advice) 클래스입니다.
 *
 * 주로 CustomException을 잡아 HTTP 응답으로 적절한 에러 메시지와 상태 코드를 반환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * CustomException이 발생했을 때 실행되는 예외 처리 메서드입니다.
     *
     * @param ex 처리할 CustomException 객체
     * @return 적절한 HTTP 상태 코드와 메시지를 포함한 ResponseEntity 객체
     */
    @ExceptionHandler(CustomException.class) //   @ExceptionHandler : CustomException이 호출되면 예외 처리 메서드를 지정하는 역할
    public ResponseEntity<ResponseDTO<String>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = HttpStatus.resolve(errorCode.getCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ResponseDTO<String> responseDTO = new ResponseDTO<>(
                null,
                status,
                errorCode.getMessage()
        );
        return ResponseEntity.status(status).body(responseDTO);
    }
}
