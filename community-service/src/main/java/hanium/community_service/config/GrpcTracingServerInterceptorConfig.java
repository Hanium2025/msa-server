package hanium.community_service.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingServerInterceptorConfig {

    @GrpcGlobalServerInterceptor
    public ServerInterceptor tracingServerInterceptor(Tracing tracing) {
        return GrpcTracing.create(tracing).newServerInterceptor();
    }
}