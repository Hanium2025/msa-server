package hanium.product_service.dto.response;

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

    public static ProductSearchResponseDTO from(List<SimpleProductDTO> list) {
        return ProductSearchResponseDTO.builder()
                .productList(list)
                .build();
    }


}
