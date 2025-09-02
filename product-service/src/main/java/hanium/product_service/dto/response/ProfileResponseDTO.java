package hanium.product_service.dto.response;

import hanium.common.proto.user.ProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileResponseDTO {
    private String nickname;
    private String profileImageUrl;

    public static ProfileResponseDTO from(ProfileResponse response) {
        return ProfileResponseDTO.builder()
                .nickname(response.getNickname())
                .profileImageUrl(response.getProfileImg())
                .build();
    }
}
