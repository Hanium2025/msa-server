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

    public TradeInfoDTO findTradeInfoByChatroomIdAndMemberId(Long chatroomId, Long memberId) {
        Chatroom chatroom = em.createQuery("""
                              select c from Chatroom c
                         where c.id = :chatroomId
                        """, Chatroom.class)
                .setParameter("chatroomId", chatroomId)
                .getSingleResult();
        //보낸 사람이 구매자 받은 사람이 판매자인데
        if (!Objects.equals(chatroom.getSenderId(), memberId) &&
                !Objects.equals(chatroom.getReceiverId(), memberId)) {
            throw new IllegalStateException("memberId가 채팅방 참가자가 아닙니다.");
        }

        Long sellerId = chatroom.getReceiverId();
        Long buyerId = chatroom.getSenderId();

        return TradeInfoDTO.builder()
                .productId(chatroom.getProductId())
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
    }
}
