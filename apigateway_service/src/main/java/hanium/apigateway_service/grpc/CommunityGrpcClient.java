package hanium.apigateway_service.grpc;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import hanium.community_service.grpc.CommunityServiceGrpc;
import hanium.community_service.grpc.Empty;
import hanium.community_service.grpc.PingResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Community Service의 gRPC 클라이언트입니다.
 *
 * Eureka에서 등록된 community_service 인스턴스를 조회하고,
 * 메타데이터로 등록된 gRPC 포트를 통해 gRPC 채널을 동적으로 생성하여 요청을 보냅니다.
 * 라운드 로빈 방식으로 인스턴스를 선택하여 간단한 로드밸런싱도 수행합니다.
 */
@Service
@RequiredArgsConstructor
public class CommunityGrpcClient {

    private final EurekaClient eurekaClient;
    private static final AtomicInteger index = new AtomicInteger(0);
    private static final Logger log = LoggerFactory.getLogger(CommunityGrpcClient.class);

    /**
     * community_service로 gRPC ping 요청을 보냅니다.
     *
     * @return 응답 메시지와 함께 어떤 인스턴스에 요청했는지 반환합니다.
     * @throws IllegalStateException 유레카에 등록된 인스턴스가 없거나, gRPC 포트 메타데이터가 없는 경우
     */
    public String ping() {
        // Eureka에서 community_service 인스턴스 목록 조회
        List<InstanceInfo> instances = eurekaClient.getInstancesByVipAddress("community_service", false);

        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("Eureka에서 community_service 인스턴스를 찾을 수 없습니다.");
        }

        // 디버깅 로그
        log.info("[gRPC Client] 인스턴스 수: {}", instances.size());
        for (InstanceInfo ins : instances) {
            log.info("[gRPC Client] 인스턴스ID: {}, host: {}, IP: {}, gRPC.port: {}",
                    ins.getInstanceId(),
                    ins.getHostName(),
                    ins.getIPAddr(),
                    ins.getMetadata().get("gRPC.port"));
        }

        // Round-robin 방식으로 인스턴스 선택
        int currentIndex = Math.abs(index.getAndIncrement() % instances.size());
        InstanceInfo selectedInstance = instances.get(currentIndex);

        // IP 주소와 gRPC 포트 가져옴
        String host = selectedInstance.getIPAddr();// Eureka에서 hostname은 컨테이너 이름
        String portStr = selectedInstance.getMetadata().get("gRPC.port");

        if (portStr == null) {
            throw new IllegalStateException("Eureka 메타데이터에 gRPC 포트 정보가 없습니다.");
        }

        int grpcPort = Integer.parseInt(portStr);
        // gRPC 채널 생성, 서비스 호출 수행
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, grpcPort)
                .usePlaintext()
                .build();

        CommunityServiceGrpc.CommunityServiceBlockingStub stub = CommunityServiceGrpc.newBlockingStub(channel);

        PingResponse response;
        try {
            response = stub.ping(Empty.newBuilder().build());
        } finally {
            //채널 종료
            channel.shutdown();
        }

        return "[인스턴스: " + selectedInstance.getHostName() + ":" + grpcPort + "] " + response.getMessage();
    }
}
