package hanium.product_service.dto.response;

import hanium.product_service.repository.projection.ProductWithFirstImage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleProductDTO {

    private Long productId;
    private String title;
    private Long price;
    private String imageUrl;

    public static SimpleProductDTO from(ProductWithFirstImage p) {
        return SimpleProductDTO.builder()
                .productId(p.getProductId())
                .title(p.getTitle())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl() == null ? "" : p.getImageUrl())
                .build();
    }
}
