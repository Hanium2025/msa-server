package hanium.apigateway_service.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterProductRequestDTO {
    private String title;
    private String content;
    private String price;
    private String sellerId;
    private String category; // ENUM 값 (ex: ELECTRONICS)
}