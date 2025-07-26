package hanium.product_service.dto.response;

import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Long price;
    private Long sellerId;
    private Status status;
    private Category category;
    private List<ProductImageDTO> images;

    public static ProductResponseDTO of(Product product, List<ProductImageDTO> images) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .content(product.getContent())
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .category(product.getCategory())
                .status(product.getStatus())
                .images(images)
                .build();
    }
}

