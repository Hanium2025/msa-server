package hanium.product_service.dto.request;

import hanium.common.proto.product.TradeReviewRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeReviewRequestDTO {
    private Long tradeId;
    private Long memberId;
    private Double rating;
    private String comment;

    public static TradeReviewRequestDTO from(TradeReviewRequest req) {
        return TradeReviewRequestDTO.builder()
                .tradeId(req.getTradeId())
                .memberId(req.getMemberId())
                .rating(req.getRating())
                .comment(req.getComment())
                .build();
    }

}
