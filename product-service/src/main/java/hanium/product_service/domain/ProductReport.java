package hanium.product_service.domain;

import hanium.product_service.dto.request.ReportProductRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "product_report")
public class ProductReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(length = 1023)
    private String details;

    public static ProductReport of(Product product, ReportProductRequestDTO req) {
        return ProductReport.builder()
                .memberId(req.getMemberId())
                .product(product)
                .reason(req.getReason())
                .details(req.getDetails())
                .build();
    }
}