package hanium.apigateway_service.controller;

import hanium.apigateway_service.grpc.CommunityGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityGrpcClient grpcClient;

    @GetMapping("/community/ping")
    public String pingCommunityService() {
        return grpcClient.ping();
    }
}
