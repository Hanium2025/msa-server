package hanium.apigateway_service.mapper;


import hanium.apigateway_service.dto.community.CreatePostRequestDTO;
import hanium.common.proto.community.CreatePostRequest;

/**
 * PostGrpcMapperForGateway는 API Gateway 레이어에서 사용하는
 * DTO와 gRPC 메시지 간 변환을 담당하는 매퍼 클래스입니다.
 * <p>
 * 주로 클라이언트 요청 DTO를 gRPC 요청 메시지로 변환하는 기능을 제공합니다.
 */
public class PostGrpcMapperForGateway {

    /**
     * CreatePostRequestDTO 객체를 gRPC의 CreatePostRequest 메시지로 변환합니다.
     *
     * @param dto API Gateway에서 받은 게시글 생성 요청 DTO
     * @return gRPC 프로토콜 메시지 CreatePostRequest
     */
    public static CreatePostRequest toGrpc(CreatePostRequestDTO dto, Long memberId) {
        return CreatePostRequest.newBuilder()
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setWriterId(memberId)
                .build();
    }
}
