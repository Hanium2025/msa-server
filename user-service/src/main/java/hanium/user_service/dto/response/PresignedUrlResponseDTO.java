package hanium.user_service.dto.response;

import lombok.Builder;

@Builder
public record PresignedUrlResponseDTO(
        String presignedUrl,
        String actualImagePath
) {
}
