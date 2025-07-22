package hanium.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    // 실제 이미지 URL (S3 등 외부 저장소 경로를 저장하는 용도)
    private String imageUrl;
}
