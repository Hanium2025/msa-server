package hanium.apigateway_service.mapper;

import hanium.apigateway_service.dto.trade.request.TradeReviewRequestDTO;
import hanium.common.proto.product.TradeRequest;
import hanium.common.proto.product.TradeReviewRequest;
import org.springframework.stereotype.Component;

@Component
public class TradeGrpcMapperForGateway {
    public TradeRequest toTradeRequestGrpc(Long chatroomId, Long memberId){
        return TradeRequest.newBuilder()
                .setChatroomId(chatroomId)
                .setMemberId(memberId)
                .build();
    }

    public TradeReviewRequest toTradeReviewRequestGrpc(Long tradeId, Long memberId, TradeReviewRequestDTO dto){
        return TradeReviewRequest.newBuilder()
                .setTradeId(tradeId)
                .setMemberId(memberId)
                .setRating(dto.getRating())
                .setComment(dto.getComment())
                .build();
    }
}
