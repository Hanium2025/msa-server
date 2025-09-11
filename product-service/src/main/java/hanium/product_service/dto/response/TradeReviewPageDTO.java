package hanium.product_service.dto.response;

import hanium.product_service.domain.TradeReview;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeReviewPageDTO {
    private String title;
    private String nickname;
}
