package hanium.common.exception;

import lombok.Getter;

/**
 * CustomException은 애플리케이션 내에서 발생하는 사용자 정의 예외를 처리하기 위한 클래스입니다.
 * 각 예외는 ErrorCode 타입의 코드와 메시지를 포함하며, 이를 통해 예외 원인을 명확하게 구분할 수 있습니다.
 *
 * RuntimeException을 상속하여 언체크 예외로 동작합니다.
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * ErrorCode를 받아 CustomException 인스턴스를 생성합니다.
     * 생성 시 ErrorCode의 메시지를 RuntimeException 메시지로 설정합니다.
     *
     * @param errorCode 예외를 나타내는 ErrorCode 객체
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}