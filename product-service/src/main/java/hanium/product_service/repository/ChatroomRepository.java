package hanium.product_service.repository;

import hanium.product_service.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    //해당 채팅방 존재 여부 확인
    @Query("""
            select c from Chatroom c
            where c.productId = :productId
              and(
              c.senderId= :senderId and c.receiverId = :receiverId)
              or(c.senderId= :receiverId and c.receiverId =:senderId)
            """)
    Optional<Chatroom> findByProductIdAndMembers(Long productId, Long senderId, Long receiverId);

    // 내가 sender거나 receiver인 채팅방을 최신 대화시간 내림차순으로
    List<Chatroom> findBySenderIdOrReceiverIdOrderByLatestContentTimeDesc(Long senderId, Long receiverId);

}

