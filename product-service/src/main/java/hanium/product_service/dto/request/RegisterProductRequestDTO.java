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
    private Long price;
    private Long sellerId;
    private Category category;

    public static RegisterProductRequestDTO from(RegisterProductRequest request) {
        return RegisterProductRequestDTO.builder()
                .sellerId(request.getSellerId())
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .category(Category.valueOf(request.getCategory()))
                .build();
    }
}
