package hanium.product_service.service;

import hanium.common.proto.product.TradeRequest;
import hanium.product_service.dto.response.TradeInfoDTO;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;


public interface TradeService {
    void directTrade(Long chatroomId, TradeInfoDTO tradeInfoDTO);
}
