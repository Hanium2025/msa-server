package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Trade;
import hanium.product_service.domain.TradeReview;
import hanium.product_service.dto.request.TradeReviewRequestDTO;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.repository.TradeReviewRepository;
import hanium.product_service.service.TradeReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeReviewServiceImpl implements TradeReviewService {
    private final TradeRepository tradeRepository;
    private final TradeReviewRepository tradeReviewRepository;

    @Override
    @Transactional
    public void tradeReview(TradeReviewRequestDTO dto){
        log.info("💡 요청된 tradeId: {}", dto.getTradeId());
        Trade trade = tradeRepository.findByIdAndDeletedAtIsNull(dto.getTradeId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));

        // 리뷰 대상자 검증
        Long targetMemberId;
        if (trade.getBuyerId().equals(dto.getMemberId())) {
            targetMemberId = trade.getSellerId();
        } else if (trade.getSellerId().equals(dto.getMemberId())) {
            targetMemberId = trade.getBuyerId();
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 중복 리뷰 방지
        boolean exists = tradeReviewRepository.existsByTradeIdAndMemberId(dto.getTradeId(), dto.getMemberId());
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }

        TradeReview tradeReview = TradeReview.of(trade, dto, targetMemberId);
        tradeReviewRepository.save(tradeReview);
    }
}
