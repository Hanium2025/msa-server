package hanium.product_service.mapper;

import hanium.common.proto.product.GetTradeReviewPageResponse;
import hanium.product_service.dto.response.TradeReviewPageDTO;

public class TradeGrpcMapper {

    public static GetTradeReviewPageResponse toGetTradeReviewPageResponseGrpc(TradeReviewPageDTO dto) {
        return GetTradeReviewPageResponse.newBuilder()
                .setTitle(dto.getTitle())
                .setNickname(dto.getNickname())
                .build();
    }
}
