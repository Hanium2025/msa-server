//package hanium.apigateway_service.config;
//
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//
//    @Bean
//    @LoadBalanced //유레카와 연동해서 서비스명을 실제 ip:port로 바꿈
//    public WebClient.Builder loadBalancedWebClientBuilder(){
//        return WebClient.builder();
//    }
//}
