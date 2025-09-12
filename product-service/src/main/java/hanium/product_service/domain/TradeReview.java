package hanium.product_service.domain;

import hanium.product_service.dto.request.TradeReviewRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "trade_review")
public class TradeReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "target_member_id")
    private Long targetMemberId;

    @Column
    private Double rating;

    @Column
    private String comment;

    public static TradeReview of(Trade trade, TradeReviewRequestDTO dto, Long targetMemberId){
        return TradeReview.builder()
                .trade(trade)
                .memberId(dto.getMemberId())
                .targetMemberId(targetMemberId)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

    }
}
