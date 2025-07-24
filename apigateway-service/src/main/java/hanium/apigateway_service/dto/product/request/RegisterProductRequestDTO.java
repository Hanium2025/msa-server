package hanium.apigateway_service.dto.product.request;

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
    private String category; // ENUM 값 (ex: ELECTRONICS)
}