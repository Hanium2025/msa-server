package hanium.product_service.repository;

import hanium.product_service.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    
    //해당 채팅방 존재 여부 확인
    Optional<Chatroom> findByProductIdAndSenderIdAndReceiverId(Long productId, Long senderId, Long receiverId);

//    //채팅방 별 최근 채팅 메시지 및 시간 업데이트
//    @Query("update Chatroom c set c.latestContent = :content, c.latestContentTime = :time where c.id= :chatroomId")
//    void updateLatest(@Param("chatroomId") Long chatroomId, @Param("content") String content, @Param("time") LocalDateTime time);


   // 내가 sender거나 receiver인 채팅방을 최신 대화시간 내림차순으로
   List<Chatroom> findBySenderIdOrReceiverIdOrderByLatestContentTimeDesc(Long senderId, Long receiverId);

}

