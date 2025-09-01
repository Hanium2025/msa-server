package hanium.product_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMainDTO {

    List<SimpleProductDTO> products;
    List<MainCategoriesDTO> categories;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MainCategoriesDTO {
        private String name;
        private String imageUrl;
    }
}
