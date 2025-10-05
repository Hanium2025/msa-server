package hanium.user_service.dto.response;

import hanium.common.proto.product.SimpleProductResponse;
import lombok.Builder;

@Builder
public record SimpleProductDTO(
        Long productId,
        String title,
        Long price,
        String imageUrl
) {
    public static SimpleProductDTO from(SimpleProductResponse res) {
        return SimpleProductDTO.builder()
                .productId(res.getProductId())
                .title(res.getTitle())
                .price(res.getPrice())
                .imageUrl(res.getImageUrl())
                .build();
    }
}
