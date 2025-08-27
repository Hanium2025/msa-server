package hanium.product_service.dto.request;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.product.RegisterProductRequest;
import hanium.product_service.domain.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterProductRequestDTO {
    private Long sellerId;
    private String title;
    private String content;
    private Long price;
    private Category category;
    private List<String> imageUrls;

    public static RegisterProductRequestDTO from(RegisterProductRequest request) {
        try {
            Category category = Category.valueOf(request.getCategory());
            return RegisterProductRequestDTO.builder()
                    .sellerId(request.getSellerId())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .price(request.getPrice())
                    .category(category)
                    .imageUrls(request.getImageUrlsList())
                    .build();
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.UNKNOWN_PRODUCT_CATEGORY);
        }
    }
}
