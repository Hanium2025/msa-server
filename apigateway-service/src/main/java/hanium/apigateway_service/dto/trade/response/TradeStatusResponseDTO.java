package hanium.apigateway_service.dto.trade.response;

import hanium.common.proto.product.GetTradeReviewPageResponse;
import hanium.common.proto.product.TradeStatusResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeStatusResponseDTO {
    private Long tradeId;
    private String status;

    public static TradeStatusResponseDTO from(TradeStatusResponse response) {
        return TradeStatusResponseDTO.builder()
                .status(response.getStatus())
                .tradeId(response.getTradeId())
                .build();
    }
}
