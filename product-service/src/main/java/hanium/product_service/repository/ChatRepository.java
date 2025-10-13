package hanium.product_service.repository;

import hanium.product_service.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Message, Long> {

    @Query("""
            select m from Message m  where m.chatroom.id = :chatroomId
            order by m.createdAt asc
            """)
    List<Message> findAllByChatroomIdOrderByCreatedAtAsc(Long chatroomId);

    //첫 페치지(커서 없음) : 최신부터
    @Query("""
        select m from Message m where m.chatroom.id = :chatRoomId
        order by m.createdAt desc, m.id desc
""")
    Slice<Message> findRecent(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable
    );

    // BEFORE : 커서 이전(과거 더 보기)
    @Query("""
    select m
    from Message m
    where m.chatroom.id = :chatRoomId
    and(  m.createdAt < :lastTimeStamp
           or
           (m.createdAt = :lastTimeStamp and m.id < :lastId
           )
    )
    order by m.createdAt desc, m.id desc 
""")
    Slice<Message> findBefore(
            @Param("chatRoomId") Long chatRoomId,
            @Param("lastTimeStamp") LocalDateTime lastTimeStamp,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    // AFTER: 커서 이후(새 메시지) — ASC로 뽑아 서비스에서 reverse하여 응답은 항상 DESC 유지
    @Query("""
        select m
        from Message m
            where m.chatroom.id = :chatRoomId
                and(
                    m.createdAt > :firstTimeStamp
                        or(m.createdAt = :firstTimeStamp and
                            m.id > :firstId)

                    )
           order by m.createdAt asc, m.id asc
    """)
    Slice<Message> findAfterAsc(
            @Param("chatRoomId") Long chatRoomId,
            @Param("firstTimeStamp") LocalDateTime firstTimeStamp,
            @Param("firstId") Long firstId,
            Pageable pageable
    );


}
