package hanium.product_service.dto.response;

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
    private Long sellerId;
    private String sellerNickname;
    private String createdAt;
    private String title;
    private String content;
    private Long price;
    private String category;
    private String status;
    private boolean seller;
    private boolean liked;
    private Long likeCount;
    private List<ProductImageDTO> images;

    public void updateSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }
}