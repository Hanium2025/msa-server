package hanium.apigateway_service.grpc;


import hanium.apigateway_service.dto.CreatePostRequestDTO;
import hanium.apigateway_service.mapper.PostGrpcMapperForGateway;
import hanium.common.proto.CommonResponse;
import hanium.common.proto.community.CommunityServiceGrpc;
import hanium.common.proto.community.CreatePostRequest;
import hanium.common.proto.community.Empty;
import hanium.common.proto.community.PingResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * Community Service의 gRPC 클라이언트입니다.
 * grpc-spring-boot-starter 기반 Eureka 연동을 통해 로드밸런싱 및 채널 관리 자동화.
 */
@Service
@RequiredArgsConstructor
public class CommunityGrpcClient {

    @GrpcClient("community_service") //discovery:///community_service 사용
    private CommunityServiceGrpc.CommunityServiceBlockingStub stub;

    public String ping() {
        PingResponse response = stub.ping(Empty.newBuilder().build());
        return "[응답 메시지]: " + response.getMessage();
    }

    public CommonResponse createPost(CreatePostRequestDTO dto) {
        CreatePostRequest grpcRequest = PostGrpcMapperForGateway.toGrpc(dto);

        return stub.createPost(grpcRequest);
    }

}
