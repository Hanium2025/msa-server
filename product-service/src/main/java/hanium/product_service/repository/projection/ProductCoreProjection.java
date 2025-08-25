package hanium.product_service.repository.projection;

import hanium.product_service.domain.Category;
import hanium.product_service.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductCoreProjection {
    private Long id;
    private String title;
    private String content;
    private Long price;
    private Long sellerId;
    private Status status;
    private Category category;
    private LocalDateTime createdAt;
    private boolean liked;
    private Long likeCount;
    private boolean seller;
}
