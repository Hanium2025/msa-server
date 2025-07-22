package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String price;
    private Long sellerId;
    private String status;
    private String category;

    public static ProductInfoResponseDTO from(ProductResponse productResponse) {
        return ProductInfoResponseDTO.builder()
                .id(productResponse.getId())
                .title(productResponse.getTitle())
                .content(productResponse.getContent())
                .price(productResponse.getPrice())
                .sellerId(productResponse.getSellerId())
                .category(productResponse.getCategory())
                .status(productResponse.getStatus())
                .build();
    }
}
