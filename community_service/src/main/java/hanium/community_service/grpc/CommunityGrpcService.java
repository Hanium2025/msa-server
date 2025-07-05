package hanium.community_service.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

@GrpcService
public class CommunityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase {
    @Value("${eureka.instance.hostname:unknown-host}")
    private String hostname;
    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        String hostNameToUse = hostname;  // @Value로 주입된 값 사용
        PingResponse response = PingResponse.newBuilder()
                .setMessage("pong from " + hostNameToUse)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
