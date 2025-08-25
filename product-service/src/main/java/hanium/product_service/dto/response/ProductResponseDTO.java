package hanium.product_service.dto.response;

import hanium.product_service.domain.Product;
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
    private String status;
    private String category;
    private List<ProductImageDTO> images;
    private boolean isSeller;
    private boolean isLiked;

    public static ProductResponseDTO of(String sellerNickname, Product product,
                                        List<ProductImageDTO> images, boolean isSeller, boolean liked) {
        return ProductResponseDTO.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .content(product.getContent())
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .sellerNickname(sellerNickname)
                .category(product.getCategory().getLabel())
                .status(product.getStatus().getLabel())
                .images(images)
                .isSeller(isSeller)
                .isLiked(liked)
                .build();
    }

    public void updateSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }
}