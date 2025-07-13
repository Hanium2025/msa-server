package hanium.community_service.mapper.grpc;

import hanium.community_service.dto.CreatePostRequestDTO;
import hanium.common.proto.community.CreatePostRequest;
/**
 * PostGrpcMapper는 gRPC 메시지와 DTO 간의 변환을 담당하는 매퍼 클래스입니다.
 *
 * gRPC 통신에서 사용하는 CreatePostRequest 메시지와
 * 애플리케이션 내부에서 사용하는 CreatePostRequestDTO 객체 간의 변환을 지원합니다.
 */
public class PostGrpcMapper {

    // gRPC → DTO
    public static CreatePostRequestDTO toDto(CreatePostRequest request) {
        return CreatePostRequestDTO.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(request.getWriterId())
                .build();
    }

    // DTO → gRPC
    public static CreatePostRequest toGrpc(CreatePostRequestDTO dto) {
        return CreatePostRequest.newBuilder()
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setWriterId(dto.getWriterId())
                .build();
    }
}
