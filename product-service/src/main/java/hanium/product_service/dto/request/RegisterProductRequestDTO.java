package hanium.product_service.dto.request;

import hanium.product_service.enums.Category;
import lombok.*;

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
}
