package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.*;
import hanium.product_service.dto.request.CreateWayBillRequestDTO;
import hanium.product_service.dto.response.DeliveryInfoResponseDTO;
import hanium.product_service.dto.response.DeliveryStatusSummaryDTO;
import hanium.product_service.repository.DeliveryRepository;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.service.DeliveryService;
import hanium.product_service.util.SweetTrackerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final TradeRepository tradeRepository;
    private final SweetTrackerUtil sweetTrackerUtil;

    @PersistenceContext
    private EntityManager em;

    /**
     * 판매자가 송장 등록을 합니다.
     *
     * @param dto
     */
    @Override
    @Transactional
    public void createWayBill(CreateWayBillRequestDTO dto){
        tradeRepository.findByIdAndDeletedAtIsNull(dto.getTradeId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));
        Trade trade = em.find(Trade.class, dto.getTradeId());

        // 판매자가 아닌 사람이 등록할 경우
        if(!trade.getSellerId().equals(dto.getMemberId()))
            throw new CustomException(ErrorCode.NO_PERMISSION);

        Delivery delivery = Delivery.of(trade, dto);
        deliveryRepository.save(delivery);
        tradeRepository.updateStatus(trade.getChatroom().getId(), TradeStatus.SHIPPED);
        log.info("✅ {} 거래 {} 송장 등록 성공", dto.getTradeId(), dto.getInvoiceNo());
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO getDeliveryInfo(Long tradeId, Long memberId){
        Delivery delivery = deliveryRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_NOT_FOUND));

        String code = delivery.getCode();
        String invoiceNo = delivery.getInvoiceNo();

        List<DeliveryStatusSummaryDTO> status =sweetTrackerUtil.fetchTrackingInfo(code, invoiceNo);

        log.info("✅ {} 택배 조회 성공", invoiceNo);
        return DeliveryInfoResponseDTO.from(code, invoiceNo, status);
    }

}
