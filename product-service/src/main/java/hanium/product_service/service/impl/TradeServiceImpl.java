package hanium.product_service.service.impl;

import hanium.product_service.domain.*;
import hanium.product_service.dto.response.TradeInfoDTO;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.service.TradeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {
    @PersistenceContext
    private EntityManager em;

    private final TradeRepository tradeRepository;

    @Transactional
    @Override
    public void directTrade(Long chatroomId, TradeInfoDTO tradeInfoDTO) {
        // 판매 처리

        Product productRef = em.getReference(Product.class, tradeInfoDTO.getProductId());
        Chatroom chatroomRef = em.getReference(Chatroom.class, chatroomId);

        Trade trade = Trade.builder()
                .buyerId(tradeInfoDTO.getBuyerId())
                .sellerId(tradeInfoDTO.getSellerId())
                .product(productRef)
                .type(TradeType.DIRECT)
                .tradeStatus(TradeStatus.REQUESTED)
                .chatroom(chatroomRef)
                .build();
        tradeRepository.save(trade);
    }

    @Transactional
    @Override
    public int acceptDirectTrade(Long chatroomId) {
        return tradeRepository.updateStatus(chatroomId, TradeStatus.ACCEPTED);


    }
}
