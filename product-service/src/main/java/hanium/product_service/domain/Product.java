package hanium.product_service.domain;

import hanium.product_service.dto.request.RegisterProductRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_product_created_at", columnList = "created_at"),
                @Index(name = "idx_product_created_at_id", columnList = "created_at, id")
        }
)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(length = 1000)
    private String content;

    @Column
    private Long price;

    @Column
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static Product from(RegisterProductRequestDTO dto) {
        return Product.builder()
                .sellerId(dto.getSellerId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .status(Status.SELLING)
                .build();
    }
}
