package hanium.product_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponseDTO {

    private List<ProductResponseDTO> productList;

    public static ProductSearchResponseDTO of(List<ProductResponseDTO> list) {
        ProductSearchResponseDTO dto = new ProductSearchResponseDTO();
        dto.productList = list;
        return dto;
    }

}
