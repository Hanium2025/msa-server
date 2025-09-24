package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Trade;
import hanium.product_service.domain.TradeReview;
import hanium.product_service.dto.request.TradeReviewRequestDTO;
import hanium.product_service.dto.response.ProfileResponseDTO;
import hanium.product_service.dto.response.TradeReviewPageDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
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
    private final ProfileGrpcClient profileGrpcClient;

    /**
     * 거래 평가 초기 화면을 위한 거래 상품 제목과 상대방 닉네임을 가져옵니다.
     *
     * @param tradeId
     * @param memberId
     * @return TradeReviewPageDTO
     */
    @Override
    @Transactional(readOnly = true)
    public TradeReviewPageDTO getTradeReviewPageInfo(Long tradeId, Long memberId){
        Trade trade = tradeRepository.findByIdWithProduct(tradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));

        String title = trade.getProduct().getTitle();

        ProfileResponseDTO profileResponseDTO = profileGrpcClient.getProfileByMemberId(trade.getOtherParty(memberId));

        return TradeReviewPageDTO.builder()
                .title(title)
                .nickname(profileResponseDTO.getNickname())
                .build();
    }

    /**
     * 거래 상대에 대해 거래 평가를 진행합니다.
     *
     * @param dto
     */
    @Override
    @Transactional
    public void tradeReview(TradeReviewRequestDTO dto){
        Trade trade = tradeRepository.findByIdAndDeletedAtIsNull(dto.getTradeId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));

        // 리뷰 대상자 검증
        Long targetMemberId = trade.getOtherParty(dto.getMemberId());

        // 중복 리뷰 방지
        boolean exists = tradeReviewRepository.existsByTradeIdAndMemberId(dto.getTradeId(), dto.getMemberId());
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }

        TradeReview tradeReview = TradeReview.of(trade, dto, targetMemberId);
        tradeReviewRepository.save(tradeReview);

        log.info("✅ {} 사용자 {}와의 {} 거래 후기 작성", dto.getMemberId(), targetMemberId, dto.getTradeId());
    }
}
