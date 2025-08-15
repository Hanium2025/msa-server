package hanium.product_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {
    @Bean
    public Region awsRegion(@Value("${spring.cloud.aws.region.static:ap-northeast-2}") String region) {
        return Region.of(region);
    }
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(
            @Value("${spring.cloud.aws.credentials.access-key:}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key:}") String secretKey
    ) {
        if (!accessKey.isBlank() && !secretKey.isBlank()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        }
        return DefaultCredentialsProvider.create(); // 환경변수/프로필/IMDS 체인 사용
    }
    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(Region region, AwsCredentialsProvider creds) {
        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(creds)
                .build();
    }
}
