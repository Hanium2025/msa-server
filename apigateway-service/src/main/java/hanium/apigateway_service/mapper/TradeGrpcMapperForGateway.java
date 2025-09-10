package hanium.apigateway_service.mapper;

import hanium.common.proto.product.TradeRequest;
import org.springframework.stereotype.Component;

@Component
public class TradeGrpcMapperForGateway {
    public TradeRequest toTradeRequestGrpc(Long chatroomId, Long memberId){
        return TradeRequest.newBuilder()
                .setChatroomId(chatroomId)
                .setMemberId(memberId)
                .build();
    }
}
