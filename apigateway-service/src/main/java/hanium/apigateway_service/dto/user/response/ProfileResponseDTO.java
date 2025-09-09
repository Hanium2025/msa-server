package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.ProfileResponse;

public record ProfileResponseDTO(
        String nickname,
        String imageUrl
) {
    public static ProfileResponseDTO from(ProfileResponse res) {
        return new ProfileResponseDTO(res.getNickname(), res.getProfileImg());
    }
}
