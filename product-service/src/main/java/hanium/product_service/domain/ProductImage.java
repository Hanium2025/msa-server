package hanium.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_image",
        indexes = {
                @Index(name = "idx_product_image_id_product_id", columnList = "product_id, id")
        }
)
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    // 실제 이미지 URL (S3 등 외부 저장소 경로를 저장하는 용도)
    @Column(length = 1000)
    private String imageUrl;

    public static ProductImage of(Product product, String imageUrl) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .build();
    }
}
