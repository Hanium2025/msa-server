package hanium.product_service.repository;

import hanium.product_service.domain.Trade;
import hanium.product_service.dto.response.CompleteTradeInfoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TradeCompleteRepository {

    @PersistenceContext
    private EntityManager em;

    public CompleteTradeInfoDTO findTradeInfoByChatroomId(Long chatroomId, Long memberId) {
        Trade trade = em.createQuery("""
                        select t from Trade t where t.chatroom.id = :chatroomId
                        """, Trade.class)
                .setParameter("chatroomId", chatroomId)
                .getSingleResult();
        Long tradeId = trade.getId();
        Long productId = trade.getProduct().getId();
        Long sellerId = trade.getSellerId();
        Long buyerId = trade.getBuyerId();
        Long opponentId = memberId.equals(sellerId) ? buyerId : sellerId;
        return CompleteTradeInfoDTO.builder().productId(productId).tradeId(tradeId).opponentId(opponentId).build();

    }
}
