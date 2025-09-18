package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.*;
import hanium.product_service.dto.request.CreateWayBillRequestDTO;
import hanium.product_service.repository.DeliveryRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.service.DeliveryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final TradeRepository tradeRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void createWayBill(CreateWayBillRequestDTO dto){
        tradeRepository.findByIdAndDeletedAtIsNull(dto.getTradeId())
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));
        Trade trade = em.getReference(Trade.class, dto.getTradeId());

        // 판매자가 아닌 사람이 등록할 경우
        if(trade.getSellerId() != dto.getMemberId())
            throw new CustomException(ErrorCode.NO_PERMISSION);

        Delivery delivery = Delivery.of(trade, dto);
        deliveryRepository.save(delivery);

        log.info("✅ {} 거래 {} 송장 등록 성공", dto.getTradeId(), dto.getInvoiceNo());
    }

}
