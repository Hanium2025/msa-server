package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.PresignedUrlResponse;

public record PresignedUrlResponseDTO(
        String presignedUrl,
        String actualImagePath
) {
    public static PresignedUrlResponseDTO from(PresignedUrlResponse protoResponse) {
        return new PresignedUrlResponseDTO(protoResponse.getPresignedUrl(), protoResponse.getImagePath());
    }
}
