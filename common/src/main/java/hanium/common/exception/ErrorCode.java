package hanium.common.exception;

import lombok.Getter;

/**
 * ErrorCode 열거형은 애플리케이션에서 발생할 수 있는 다양한 오류 상황을
 * 코드와 메시지로 정의합니다.
 * <p>
 * 각 상수는 HTTP 상태 코드와 사용자에게 보여질 메시지를 포함합니다.
 */

@Getter
public enum ErrorCode {
    INVALID_INPUT(400, "잘못된 입력입니다."),
    POST_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    INTERNAL_ERROR(500, "서버 오류입니다. 로그를 확인해주세요."),

    // -- USER-SERVICE -- //
    HAS_EMAIL(400, "이미 존재하는 이메일입니다."),
    HAS_PHONE(400, "이미 존재하는 전화번호입니다."),
    PASSWORD_NOT_MATCH(400, "재확인 비밀번호가 일치하지 않습니다."),
    INVALID_CONTENT_TYPE(400, "요청은 application/json 타입이어야 합니다."),
    NULL_ACCESS_TOKEN(400, "Access 토큰이 존재하지 않습니다."),
    TOKEN_NOT_BEARER(400, "토큰은 Bearer로 시작해야 합니다."),
    LOGIN_FAILED(401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(404, "해당하는 Member를 찾을 수 없습니다."),
    AUTHORITY_NOT_FOUND(404, "사용자의 권한을 조회할 수 없습니다."),
    REFRESH_NOT_FOUND(404, "데이터베이스에서 Refresh 토큰을 찾을 수 없습니다."),

    // -- PRODUCT-SERVICE -- //
    ERROR_ADD_PRODUCT(500, "상품 등록 중 문제가 발생했습니다."),
    PRODUCT_NOT_FOUND(404, "해당하는 상품을 찾을 수 없습니다."),
    UNKNOWN_SORT(400, "sort 파라미터 값은 recent 혹은 like만 가능합니다."),
    UNKNOWN_PRODUCT_CATEGORY(400, "입력할 수 있는 상품 카테고리 범위가 아닙니다."),
    NO_PERMISSION(403, "해당 상품 등록자가 아니므로 권한이 없습니다."),
    BLANK_IMAGE(400, "이미지가 요청되었으나, 서버에 빈 파일이 전송되었습니다."),
    IMAGE_EXCEEDED(404, "이미지는 최대 5개까지 업로드 가능합니다."),
    IMAGE_UPLOAD_ERROR(500, "이미지 업로드에 실패했습니다."),
    IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다."),
    REDIS_BOUNDZSET_ERROR(500, "레디스 연산자를 불러오는 데에 실패했습니다."),
    RECENT_VIEW_SERVER_ERROR(500, "상품 조회 기록 저장 중 오류가 발생했습니다."),
    CHATROOM_NOT_FOUND(404, "해당 아이디를 가진 채팅방을 찾을 수 없습니다."),
    INVALID_CHAT_IMAGE_REQUEST(404, "이미지는 최소 0장 부터 최대 3장까지 가능합니다."),
    CHATROOM_ID_NOT_FOUND(400, "해당 아이디를 가진 채팅방이 없습니다."),
    NOT_IMAGE(400,"타입이 이미지가 아닙니다."),
    ELASTICSEARCH_ERROR(500, "검색 중 오류가 발생했습니다."),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

