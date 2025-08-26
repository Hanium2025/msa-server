package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.CategoryMain;
import hanium.common.proto.product.ProductMainResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMainDTO {

    List<SimpleProductDTO> products;
    List<MainCategoriesDTO> categories;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class MainCategoriesDTO {
        private String name;
        private String imageUrl;

        public static MainCategoriesDTO from(CategoryMain message) {
            return MainCategoriesDTO.builder()
                    .name(message.getName())
                    .imageUrl(message.getImageUrl())
                    .build();
        }
    }

    public static ProductMainDTO from(ProductMainResponse message) {
        return ProductMainDTO.builder()
                .products(message
                        .getProductsList()
                        .stream()
                        .map(SimpleProductDTO::from)
                        .collect(Collectors.toList()))
                .categories(message
                        .getCategoriesList()
                        .stream()
                        .map(MainCategoriesDTO::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
