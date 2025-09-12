package hanium.apigateway_service.dto.product.response;

import hanium.common.proto.product.GetTradeReviewPageResponse;
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

    public static TradeReviewPageDTO from(GetTradeReviewPageResponse response) {
        return TradeReviewPageDTO.builder()
                .title(response.getTitle())
                .nickname(response.getNickname())
                .build();
    }
}
