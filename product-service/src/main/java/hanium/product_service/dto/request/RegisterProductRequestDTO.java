package hanium.product_service.dto.request;

import hanium.common.proto.product.RegisterProductRequest;
import hanium.product_service.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RegisterProductRequestDTO {
    private String title;
    private String content;
    private String price;
    private Long sellerId;
    private Category category;

    public static RegisterProductRequestDTO from(RegisterProductRequest request) {
        return RegisterProductRequestDTO.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .sellerId(request.getSellerId())
                .category(Category.valueOf(request.getCategory()))
                .build();
    }
}
