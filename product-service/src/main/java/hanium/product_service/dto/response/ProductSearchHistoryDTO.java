package hanium.product_service.dto.response;

import hanium.product_service.domain.ProductSearch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSearchHistoryDTO {
    private Long searchId;
    private String keyword;

    public static ProductSearchHistoryDTO from(ProductSearch productSearch) {
        return new ProductSearchHistoryDTO(productSearch.getId(), productSearch.getKeyword());
    }
}
