package hanium.community_service.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class CommnuityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase{
    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        PingResponse response = PingResponse.newBuilder()
                .setMessage("pong from community-service")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
