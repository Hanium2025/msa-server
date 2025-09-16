package hanium.product_service.repository;

import hanium.product_service.domain.TradeStatus;
import hanium.product_service.dto.response.CompleteTradeInfoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import hanium.product_service.domain.Trade;

import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    Optional<Trade> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT t FROM Trade t JOIN FETCH t.product WHERE t.id = :tradeId")
    Optional<Trade> findByIdWithProduct(@Param("tradeId") Long tradeId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
             update Trade t set t.tradeStatus= :status where t.chatroom.id = :chatroomId 
             and (t.tradeStatus = hanium.product_service.domain.TradeStatus.REQUESTED or t.tradeStatus = hanium.product_service.domain.TradeStatus.ACCEPTED)
            """)
    int updateStatus(@Param("chatroomId") Long chatroomId, @Param("status") TradeStatus status);

    @Query("""
            SELECT t.tradeStatus FROM Trade t WHERE t.chatroom.id = :chatroomId 
                       and (t.buyerId = :memberId or t.sellerId = :memberId)
            """)
    Optional<TradeStatus> findTradeStatus(@Param("chatroomId") Long chatroomId, @Param("memberId")Long memberId);

}
