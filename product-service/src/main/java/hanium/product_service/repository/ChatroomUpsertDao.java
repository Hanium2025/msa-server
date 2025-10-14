package hanium.product_service.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatroomUpsertDao {

    private final JdbcTemplate jdbc;

    /**
     * 중복이면 기존 행의 id를, 신규면 새 id를 돌려줌.
     * 같은 커넥션에서 SELECT LAST_INSERT_ID() 해야 하므로 @Transactional 필요.
     */
    @Transactional
    public long upsertAndGetId(long productId, long senderId, long receiverId) {
        jdbc.update("""
            INSERT INTO chatroom (product_id, sender_id, receiver_id, latest_content_time, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW(), NOW())
            ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)
        """, productId, senderId, receiverId);

        // LAST_INSERT_ID()는 커넥션 스코프라 같은 트랜잭션/커넥션이어야 정확함
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id != null ? id : 0L;
    }
}
