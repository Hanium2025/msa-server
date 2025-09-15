package hanium.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade; //연관된 거래

    @Column(nullable = false, unique = true)
    private String tossOrderKey; //토스페이먼츠의 결제 식별 키

    @Column(nullable = false)
    private String tossOrderId; //프론트에서 전달된 orderId

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    PaymentMethod paymentMethod;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus paymentStatus;
}
