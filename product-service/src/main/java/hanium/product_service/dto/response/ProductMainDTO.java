package hanium.product_service.dto.response;

import hanium.product_service.domain.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMainDTO {

    List<MainProductsDTO> products;
    List<MainCategoriesDTO> categories;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MainProductsDTO {
        private Long productId;
        private String title;
        private Long price;
        private String imageUrl;

        public static MainProductsDTO from(Product product, String imageUrl) {
            return MainProductsDTO.builder()
                    .productId(product.getId())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MainCategoriesDTO {
        private String name;
        private String imageUrl;
    }
}
