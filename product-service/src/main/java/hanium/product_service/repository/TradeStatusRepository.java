package hanium.product_service.repository;

import hanium.product_service.domain.Trade;
import hanium.product_service.dto.response.CompleteTradeInfoDTO;
import hanium.product_service.dto.response.TradeStatusDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class TradeStatusRepository {

    @PersistenceContext
    private EntityManager em;

    public TradeStatusDTO findTradeStatusByChatroomId(Long chatroomId, Long memberId) {
        Trade trade = em.createQuery("""
                        select t from Trade t where t.chatroom.id = :chatroomId
                        """, Trade.class)
                .setParameter("chatroomId", chatroomId)
                .getSingleResult();
        Long tradeId = trade.getId();
        String status = trade.getTradeStatus().toString();
        return TradeStatusDTO.builder().tradeId(tradeId).status(status).build();

    }
}
