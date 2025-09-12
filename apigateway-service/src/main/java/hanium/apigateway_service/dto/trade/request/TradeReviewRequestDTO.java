package hanium.apigateway_service.dto.trade.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeReviewRequestDTO {
    private Double rating;
    private String comment;

}
