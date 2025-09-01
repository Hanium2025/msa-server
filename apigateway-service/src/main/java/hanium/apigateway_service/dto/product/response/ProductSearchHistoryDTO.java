package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductSearchHistory;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSearchHistoryDTO {
    private Long searchId;
    private String keyword;

    public static ProductSearchHistoryDTO from(ProductSearchHistory message) {
        return new ProductSearchHistoryDTO(message.getSearchId(), message.getKeyword());
    }
}
