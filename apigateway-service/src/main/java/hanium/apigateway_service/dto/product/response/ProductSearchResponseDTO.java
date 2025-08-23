package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponseDTO {
    private List<ProductResponseDTO> productList;

    public static ProductSearchResponseDTO from(ProductSearchResponse grpcResponse) {
        List<ProductResponseDTO> dtoList = grpcResponse.getProductListList().stream()
                .map(ProductResponseDTO::from)
                .collect(Collectors.toList());

        return ProductSearchResponseDTO.builder()
                .productList(dtoList)
                .build();
    }

}
