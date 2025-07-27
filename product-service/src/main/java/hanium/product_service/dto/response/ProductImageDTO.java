package hanium.product_service.dto.response;

import hanium.product_service.domain.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Long productImageId;
    private String imageUrl;

    public static ProductImageDTO from(ProductImage productImage) {
        return ProductImageDTO.builder()
                .productImageId(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .build();
    }
}
