package hanium.apigateway_service.controller;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class HealthCheckController {

    private final WebClient webClient;

    // @LoadBalanced 된 WebClient.Builder로 생성한 WebClient 주입
    public HealthCheckController(@LoadBalanced WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/user/health-check")
    public ResponseEntity<String> userHealthCheck(){
        try{
            String response = webClient.get()
                    .uri("http://user-service/api/members/health-check")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("사용자 서비스 사용 불가 : " + e.getMessage());
        }
    }

}
