package hanium.product_service.service;

import hanium.common.proto.product.TradeRequest;
import hanium.product_service.domain.TradeStatus;
import hanium.product_service.dto.response.CompleteTradeInfoDTO;
import hanium.product_service.dto.response.TradeInfoDTO;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;


public interface TradeService {
    //직거래 요청
    void directTrade(Long chatroomId, TradeInfoDTO tradeInfoDTO);
    //직거래 수락
    int acceptDirectTrade(Long chatroomId);

    //택배 거래 요청
    void parcelTrade(Long chatroomId, TradeInfoDTO tradeInfoDTO);
    //거래 진행 상태 조회
    TradeStatus getTradeStatus(Long chatroomId, Long memberId);

    //거래 완료
    CompleteTradeInfoDTO completeTrade(Long chatroomId, Long memberId);
}
