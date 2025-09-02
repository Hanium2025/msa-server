package hanium.user_service.dto.response;

import hanium.user_service.domain.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileResponseDTO {
    private String nickname;
    private String profileImg;

    public static ProfileResponseDTO from(Profile profile) {
        return ProfileResponseDTO.builder()
                .nickname(profile.getNickname())
                .profileImg(profile.getImageUrl())
                .build();
    }
}
