package hanium.community_service.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;

@GrpcService
public class CommunityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase {
    @Value("${eureka.instance.hostname:unknown-host}")
    private String hostname;
    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        //String hostname = "unknown-host";
        //String hostNameToUse = hostname;  // @Value로 주입된 값 사용
//        try {
//            //현재 gRPC 서버가 실행 중인 머신의 이름 즉, 호스트네임을 알아내서 응답 메시지에 붙이기 위해 사용
//            hostname = InetAddress.getLocalHost().getHostName();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        String hostNameToUse = hostname;  // @Value로 주입된 값 사용
        PingResponse response = PingResponse.newBuilder()
                .setMessage("pong from " + hostNameToUse)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
