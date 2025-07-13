package hanium.user_service.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

// 커스텀 에러코드로 ResponseEntity 객체 빌드
@Data
@Builder
public class ErrorResponseEntity {
    private int httpStatus;
    private String errorName;
    private String errorCode;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .httpStatus(errorCode.getHttpStatus().value())
                        .errorName(errorCode.name())
                        .errorCode(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
