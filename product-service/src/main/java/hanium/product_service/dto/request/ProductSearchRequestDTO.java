package hanium.product_service.dto.request;

import hanium.common.proto.product.ProductSearchRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequestDTO {
    private Long memberId;
    private String keyword;

    public static ProductSearchRequestDTO from(ProductSearchRequest request) {
        return ProductSearchRequestDTO.builder()
                .memberId(request.getMemberId())
                .keyword(request.getKeyword())
                .build();

    }
}
