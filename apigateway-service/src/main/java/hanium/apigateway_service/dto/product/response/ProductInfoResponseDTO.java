package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long price;
    private Long sellerId;
    private String status;
    private String category;
    private List<String> images;

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

    public static ProductInfoResponseDTO of(ProductResponse productResponse, List<String> images) {
        return ProductInfoResponseDTO.builder()
                .id(productResponse.getId())
                .title(productResponse.getTitle())
                .content(productResponse.getContent())
                .price(productResponse.getPrice())
                .sellerId(productResponse.getSellerId())
                .category(productResponse.getCategory())
                .status(productResponse.getStatus())
                .images(images)
                .build();
    }
}
