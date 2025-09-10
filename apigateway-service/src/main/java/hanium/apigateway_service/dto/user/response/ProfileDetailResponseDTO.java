package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.ProfileDetailResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileDetailResponseDTO(
        Long memberId,
        String nickname,
        String imageUrl,
        Long score,
        List<String> mainCategory,
        boolean agreeMarketing,
        boolean agree3rdParty
) {
    public static ProfileDetailResponseDTO from(ProfileDetailResponse response) {
        return ProfileDetailResponseDTO.builder()
                .memberId(response.getMemberId())
                .nickname(response.getNickname())
                .imageUrl(response.getImageUrl())
                .score(response.getScore())
                .mainCategory(response.getMainCategoryList().stream().toList())
                .agreeMarketing(response.getAgreeMarketing())
                .agree3rdParty(response.getAgree3RdParty())
                .build();
    }
}
