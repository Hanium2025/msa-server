package hanium.apigateway_service.dto.user.response;

import hanium.apigateway_service.dto.product.response.SimpleProductDTO;
import hanium.common.proto.user.GetOtherProfileResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record OtherProfileResponseDTO(
        Long memberId,
        String nickname,
        String imageUrl,
        Long score,
        List<String> mainCategory,
        List<SimpleProductDTO> products
) {
    public static OtherProfileResponseDTO from(GetOtherProfileResponse response) {
        return OtherProfileResponseDTO.builder()
                .memberId(response.getMemberId())
                .nickname(response.getNickname())
                .imageUrl(response.getImageUrl())
                .score(response.getScore())
                .mainCategory(response.getMainCategoryList().stream().toList())
                .products(response.getProductsList().stream().map(SimpleProductDTO::from).toList())
                .build();
    }
}
