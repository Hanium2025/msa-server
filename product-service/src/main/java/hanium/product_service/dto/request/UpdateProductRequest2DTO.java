package hanium.product_service.dto.request;

import hanium.common.proto.product.UpdateProductRequest2;
import hanium.product_service.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest2DTO {

    private Long productId;
    private String title;
    private String content;
    private Long price;
    private Category category;
    private List<String> imageUrls;

    public static UpdateProductRequest2DTO from(UpdateProductRequest2 request) {
        return UpdateProductRequest2DTO.builder()
                .productId(request.getProductId())
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .category(Category.valueOf(request.getCategory()))
                .imageUrls(request.getImageUrlsList())
                .build();
    }
}
