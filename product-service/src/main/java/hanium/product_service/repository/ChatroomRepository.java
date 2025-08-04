package hanium.product_service.repository;

import hanium.product_service.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    //해당 채팅방 존재 여부 확인
    Optional<Chatroom> findByProductIdAndSenderIdAndReceiverId(Long productId, Long senderId, Long receiverId);
}

