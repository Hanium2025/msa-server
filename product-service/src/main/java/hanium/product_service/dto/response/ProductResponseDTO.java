package hanium.product_service.dto.response;

import hanium.product_service.domain.Product;
import hanium.product_service.domain.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponseDTO {

    private Long productId;
    private String title;
    private String content;
    private Long price;
    private Long sellerId;
    private String sellerNickname;
    private Status status;
    private String category;
    private List<ProductImageDTO> images;
    private boolean isSeller;

    public static ProductResponseDTO of(String sellerNickname, Product product,
                                        List<ProductImageDTO> images, boolean isSeller) {
        return ProductResponseDTO.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .content(product.getContent())
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .sellerNickname(sellerNickname)
                .category(product.getCategory().getLabel())
                .status(product.getStatus())
                .images(images)
                .isSeller(isSeller)
                .build();
    }
}

