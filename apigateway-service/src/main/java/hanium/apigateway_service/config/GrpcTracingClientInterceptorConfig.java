package hanium.apigateway_service.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ClientInterceptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingClientInterceptorConfig {
    // Brave의 Tracing 객체를 주입받아 GrpcTracing을 생성
    @GrpcGlobalClientInterceptor
    public ClientInterceptor tracingClientInterceptor(Tracing tracing) {
        return GrpcTracing.create(tracing).newClientInterceptor();
    }
}
