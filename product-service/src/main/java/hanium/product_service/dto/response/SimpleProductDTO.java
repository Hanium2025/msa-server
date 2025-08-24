package hanium.product_service.dto.response;

import hanium.product_service.domain.Product;
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

    public static SimpleProductDTO from(Product product, String imageUrl) {
        return SimpleProductDTO.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .imageUrl(imageUrl)
                .build();
    }
}
