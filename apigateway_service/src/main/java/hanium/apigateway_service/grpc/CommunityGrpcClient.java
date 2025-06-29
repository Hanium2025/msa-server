package hanium.apigateway_service.grpc;

import hanium.community_service.grpc.CommunityServiceGrpc;
import hanium.community_service.grpc.Empty;
import hanium.community_service.grpc.PingResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommunityGrpcClient  {

    @GrpcClient("community-service") // application.yml 혹은 properties 에 등록한 서비스 이름
    private CommunityServiceGrpc.CommunityServiceBlockingStub stub;

    public String ping() {
        // Empty 메시지 생성
        Empty request = Empty.newBuilder().build();

        // gRPC 호출
        PingResponse response = stub.ping(request);

        // 결과 반환
        return response.getMessage();
    }
}
