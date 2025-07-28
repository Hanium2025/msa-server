package hanium.product_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class ZipkinSenderConfig {

    @Value("${ZIPKIN_ENDPOINT:http://zipkin:9411/api/v2/spans}")
    private String zipkinEndpoint;

    @Bean
    public Sender zipkinSender() {
        return OkHttpSender.create(zipkinEndpoint);
    }
}
