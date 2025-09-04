package hanium.product_service.repository;

import hanium.product_service.domain.Chatroom;
import hanium.product_service.dto.response.TradeInfoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@Slf4j
public class ChatroomTradeInfoRepository {

    @PersistenceContext
    private EntityManager em;

    public TradeInfoDTO findTradeInfoByChatroomId(Long chatroomId, Long buyerId) {
        Chatroom chatroom = em.createQuery("""
                              select c from Chatroom c
                         where c.id = :chatroomId
                        """, Chatroom.class)
                .setParameter("chatroomId", chatroomId)
                .getSingleResult();
        if (!Objects.equals(chatroom.getSenderId(), buyerId) &&
                !Objects.equals(chatroom.getReceiverId(), buyerId)) {
            throw new IllegalStateException("buyerId가 채팅방 참가자가 아닙니다.");
        }
        Long sellerId = chatroom.getSenderId().equals(buyerId)
                ? chatroom.getReceiverId() : chatroom.getSenderId();

        return TradeInfoDTO.builder()
                .productId(chatroom.getProductId())
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
    }
}
