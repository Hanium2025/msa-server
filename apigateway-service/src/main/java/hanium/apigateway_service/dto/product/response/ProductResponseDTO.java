package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.ProductResponse;
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
public class ProductResponseDTO {
    private Long productId;
    private Long sellerId;
    private String sellerNickname;
    private String title;
    private String content;
    private Long price;
    private String category;
    private String status;
    private boolean isSeller;
    private List<ProductImageResponseDTO> images;

    public static ProductResponseDTO from(ProductResponse productResponse) {
        return ProductResponseDTO.builder()
                .productId(productResponse.getProductId())
                .sellerId(productResponse.getSellerId())
                .sellerNickname(productResponse.getSellerNickname())
                .title(productResponse.getTitle())
                .content(productResponse.getContent())
                .price(productResponse.getPrice())
                .category(productResponse.getCategory())
                .status(productResponse.getStatus())
                .images(productResponse.getImagesList().stream().map(
                        ProductImageResponseDTO::from).collect(Collectors.toList()))
                .isSeller(productResponse.getIsSeller())
                .build();
    }
}
