package hanium.apigateway_service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 응답에 사용되는 공용 DTO입니다.
 * code와 msg는 HttpStatus를 통해 설정합니다.
 * @param <T>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {

    private final int code;        // HTTP 상태 코드 (200, 400 등)
    private final String message;  // 상태 메시지 (OK, Bad Request 등)
    private final T data;          // 실제 응답 데이터 (ex. CommonResponse)

    // 2개 인자 생성자
    public ResponseDTO(T data, HttpStatus status) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.data = data;
    }

    // 3개 인자 생성자
    public ResponseDTO(T data, HttpStatus status, String message) {
        this.code = status.value();
        this.message = message;
        this.data = data;
    }
}
