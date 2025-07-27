package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponseDTO {
    private Long productImageId;
    private String imageUrl;

    public static ProductImageResponseDTO from(ProductImageResponse response) {
        return ProductImageResponseDTO.builder()
                .productImageId(response.getProductImageId())
                .imageUrl(response.getImageUrl())
                .build();
    }
}
