package hanium.apigateway_service.mapper;

import hanium.apigateway_service.dto.trade.request.CreateWayBillRequestDTO;
import hanium.apigateway_service.dto.trade.request.TradeReviewRequestDTO;
import hanium.common.proto.product.*;
import org.springframework.stereotype.Component;

@Component
public class TradeGrpcMapperForGateway {
    public TradeRequest toTradeRequestGrpc(Long chatroomId, Long memberId){
        return TradeRequest.newBuilder()
                .setChatroomId(chatroomId)
                .setMemberId(memberId)
                .build();
    }

    public GetTradeReviewPageRequest toGetTradeReviewPageRequestGrpc(Long tradeId, Long memberId){
        return GetTradeReviewPageRequest.newBuilder()
                .setTradeId(tradeId)
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

    public CreateWayBillRequest toCreateWayBillRequestGrpc(Long tradeId, Long memberId, CreateWayBillRequestDTO dto){
        return CreateWayBillRequest.newBuilder()
                .setTradeId(tradeId)
                .setMemberId(memberId)
                .setCode(dto.getCode())
                .setInvoiceNumber(dto.getInvoiceNo())
                .build();
    }

    public GetDeliveryInfoRequest toGetDeliveryInfoRequestGrpc(Long tradeId, Long memberId){
        return GetDeliveryInfoRequest.newBuilder()
                .setTradeId(tradeId)
                .setMemberId(memberId)
                .build();
    }
}
