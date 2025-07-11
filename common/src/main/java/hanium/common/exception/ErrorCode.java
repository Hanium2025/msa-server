package hanium.common.exception;

import lombok.Getter;

/**
 * ErrorCode 열거형은 애플리케이션에서 발생할 수 있는 다양한 오류 상황을
 * 코드와 메시지로 정의합니다.
 *
 * 각 상수는 HTTP 상태 코드와 사용자에게 보여질 메시지를 포함합니다.
 */

@Getter
public enum ErrorCode {
    INVALID_INPUT(400, "잘못된 입력입니다."),
    POST_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    INTERNAL_ERROR(500, "서버 오류입니다."),

    NOT_AUTHORIZED(401, "인증되지 않은 요청입니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    TOKEN_AUTH_ERROR(500, "토큰 인증 중 오류가 발생했습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

