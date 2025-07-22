package hanium.product_service.dto.response;

import hanium.product_service.enums.Category;
import hanium.product_service.enums.Status;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductInfoResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String price;
    private Long sellerId;
    private Status status;
    private Category category;
}

