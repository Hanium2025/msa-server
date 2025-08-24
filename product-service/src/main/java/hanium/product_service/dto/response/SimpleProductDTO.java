package hanium.product_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleProductDTO {

    private Long productId;
    private String title;
    private Long price;
    private String imageUrl;
}
