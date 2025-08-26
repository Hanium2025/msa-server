package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.SimpleProductResponse;
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

    public static SimpleProductDTO from(SimpleProductResponse response) {
        return SimpleProductDTO.builder()
                .productId(response.getProductId())
                .title(response.getTitle())
                .price(response.getPrice())
                .imageUrl(response.getImageUrl())
                .build();
    }
}
