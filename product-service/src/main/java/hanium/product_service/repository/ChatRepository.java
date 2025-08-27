package hanium.product_service.repository;

import hanium.product_service.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Message, Long> {

    @Query("""
            select m from Message m  where m.chatroom.id = :chatroomId
            order by m.createdAt asc
            """)
    List<Message> findAllByChatroomIdOrderByCreatedAtAsc(Long chatroomId);
}
