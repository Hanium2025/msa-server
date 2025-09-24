package hanium.user_service.dto.response;

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
}
