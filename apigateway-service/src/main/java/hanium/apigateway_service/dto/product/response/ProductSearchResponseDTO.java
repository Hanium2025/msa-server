package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductSearchResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSearchResponseDTO {

    private List<SimpleProductDTO> productList;

    public static ProductSearchResponseDTO from(ProductSearchResponse grpcResponse) {
        List<SimpleProductDTO> productList = grpcResponse
                .getProductListList()
                .stream()
                .map(SimpleProductDTO::from)
                .toList();
        return ProductSearchResponseDTO.builder()
                .productList(productList)
                .build();
    }
}
