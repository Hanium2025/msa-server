package hanium.product_service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresignService {
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region:ap-northeast-2}")
    private String region;

    @Value("${spring.cloud.chat.upload.presign-ttl-seconds:300}")
    private long ttlSeconds;

    @Value("${spring.cloud.chat.upload.max-images-per-message:3}")
    private int maxPerMsg;

    private final S3Presigner presigner;

    public List<Presigned> issue(long chatroomId, int count, String contentType) {
        if (count < 1 || count > maxPerMsg) {
            throw new IllegalArgumentException("count must be 1.." + maxPerMsg);
        }
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("images only");
        }

        List<Presigned> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String key = "chat/%d/%s%s".formatted(chatroomId, UUID.randomUUID(), ext(contentType));

            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PresignedPutObjectRequest p = presigner.presignPutObject(b -> b
                    .signatureDuration(Duration.ofSeconds(ttlSeconds))
                    .putObjectRequest(put)
            );

            // 공개 버킷 기준 GET URL
            String getUrl = "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, key);

            out.add(new Presigned(p.url().toString(), getUrl, key));
        }
        return out;
    }

    private static String ext(String ct) {
        return switch (ct) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default -> ".bin";
        };
    }

    public record Presigned(String putUrl, String getUrl, String key) {}
}
