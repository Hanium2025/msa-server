package hanium.user_service.util;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.config.AwsConfig;
import hanium.user_service.dto.request.GetPresignedUrlRequestDTO;
import hanium.user_service.dto.response.PresignedUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(3);

    private final S3Presigner s3Presigner;
    private final AwsConfig awsConfig;

    public PresignedUrlResponseDTO getPresignedUrl(GetPresignedUrlRequestDTO dto) {
        if (dto.contentType().isBlank() || !dto.contentType().startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_PROFILE_IMAGE_TYPE);
        }

        String imageKey = generateImageKey(dto.memberId()) + extension(dto.contentType());
        PutObjectPresignRequest presignRequest = buildPresignedRequest(imageKey);

        // presigned url
        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        // 실제 조회에 사용되는 url
        String actualImagePath = awsConfig.getDomain() + imageKey;

        return PresignedUrlResponseDTO.builder()
                .presignedUrl(presignedUrl)
                .actualImagePath(actualImagePath)
                .build();
    }

    // 이미지 키 생성
    private String generateImageKey(Long memberId) {
        return memberId.toString() + "/" + UUID.randomUUID();
    }

    // presigned url 요청 생성
    private PutObjectPresignRequest buildPresignedRequest(String key) {
        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(awsConfig.getBucket())
                .key("profile_image/" + key);
        return PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRATION)
                .putObjectRequest(requestBuilder.build())
                .build();
    }

    private String extension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".bin";
        };
    }
}
