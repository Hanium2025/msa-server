package hanium.apigateway_service.dto;

import hanium.common.proto.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * CommonResponseDTO는 gRPC 통신에서 전달되는 CommonResponse 프로토콜 버퍼 메시지를
 * 애플리케이션 내에서 사용하기 위한 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 Lombok 어노테이션을 활용하여 생성자, 빌더, Getter 메서드를 자동 생성합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponseDTO {
    private boolean success;
    private String message;
    private int errorCode;

    /**
     * 프로토콜 버퍼로부터 CommonResponseDTO 객체를 생성하는 정적 팩토리 메서드.
     *
     * @param proto gRPC 통신에서 받은 CommonResponse 객체
     * @return 변환된 CommonResponseDTO 객체
     */
    public static CommonResponseDTO fromProto(CommonResponse proto) {
        return CommonResponseDTO.builder()
                .success(proto.getSuccess())
                .message(proto.getMessage())
                .errorCode(proto.getErrorCode())
                .build();
    }
}