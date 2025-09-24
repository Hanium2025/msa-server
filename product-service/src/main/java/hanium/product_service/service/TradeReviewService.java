package hanium.product_service.service;

import hanium.product_service.dto.request.TradeReviewRequestDTO;
import hanium.product_service.dto.response.TradeReviewPageDTO;

public interface TradeReviewService {
    TradeReviewPageDTO getTradeReviewPageInfo(Long tradeId, Long memberId);
    void tradeReview(TradeReviewRequestDTO dto);
}
