package hanium.product_service.dto.request;

import hanium.common.proto.product.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDTO {
    private String title;
    private String content;
    private Long price;
    private String category;

    public static UpdateProductRequestDTO from(UpdateProductRequest request) {
        return UpdateProductRequestDTO.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
    }
}
