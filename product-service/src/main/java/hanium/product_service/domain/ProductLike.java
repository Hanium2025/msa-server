package hanium.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "product_like",
        indexes = {
                @Index(name = "idx_product_like_member_product", columnList = "member_id, product_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_product_like_member_product", columnNames = {"member_id", "product_id"})
        }
)
public class ProductLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, name = "member_id")
    private Long memberId;
}
