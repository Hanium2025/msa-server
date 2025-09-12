package hanium.user_service.dto.request;

import hanium.common.proto.user.GetPresignedUrlRequest;
import lombok.Builder;

@Builder
public record GetPresignedUrlRequestDTO(
        Long memberId,
        String contentType
) {
    public static GetPresignedUrlRequestDTO from(GetPresignedUrlRequest request) {
        return new GetPresignedUrlRequestDTO(request.getMemberId(), request.getContentType());
    }
}
