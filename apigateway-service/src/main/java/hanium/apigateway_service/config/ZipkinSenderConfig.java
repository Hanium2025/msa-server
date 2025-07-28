package hanium.apigateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class ZipkinSenderConfig {

    @Bean
    public Sender zipkinSender() {
        return OkHttpSender.create("http://zipkin:9411/api/v2/spans");
    }
}
