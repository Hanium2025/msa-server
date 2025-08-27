package hanium.product_service.dto.request;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.product.GetProductByCategoryRequest;
import hanium.product_service.domain.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProductByCategoryRequestDTO {
    private Long memberId;
    private Category category;
    private String sort;
    private int page;

    public static GetProductByCategoryRequestDTO from(GetProductByCategoryRequest req) {
        try {
            Category category = Category.valueOf(req.getCategory());
            return GetProductByCategoryRequestDTO.builder()
                    .memberId(req.getMemberId())
                    .category(category)
                    .sort(req.getSort())
                    .page(req.getPage())
                    .build();
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.UNKNOWN_PRODUCT_CATEGORY);
        }
    }
}
