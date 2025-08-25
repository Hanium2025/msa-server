package hanium.product_service.repository;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RecentViewRepository {

    private final RedisTemplate<Long, Long> redisTemplate;

    /**
     * key를 이용해 해당하는 Sorted set을 반환합니다.
     *
     * @param key Sorted set의 key
     * @return 해당하는 Sorted set
     */
    private BoundZSetOperations<Long, Long> getOperations(Long key) {
        try {
            return redisTemplate.boundZSetOps(key);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_BOUNDZSET_ERROR);
        }
    }

    /**
     * 특정 사용자의 sorted set에, 특정 상품을 조회했다는 기록을 현재 시간으로 저장합니다.
     *
     * @param memberId  사용자 아이디
     * @param productId 상품 아이디
     */
    public void add(Long memberId, Long productId) {
        BoundZSetOperations<Long, Long> zSet = getOperations(memberId);
        double score = (double) Instant.now().toEpochMilli();
        Boolean newlyAdded = zSet.add(productId, score);
        // 새로 들어와서 길이가 늘어난 경우 30개 이하로 유지
        if (Boolean.TRUE.equals(newlyAdded)) {
            Long size = zSet.size();
            if (size != null && size > 30) {
                // 오래된(점수 낮은) 것부터 제거
                zSet.removeRange(0, size - 30 - 1);
                log.info("❎ 오래된 기록 삭제됨");
            }
        }
        log.info("✅ 상품 조회 기록 완료: memberId={}, productId={}, score={}", memberId, productId, score);
    }

    /**
     * 최근 조회한 상품 아이디 목록을 반환합니다.
     * 없는 경우 빈 리스트를 반환합니다.
     *
     * @param memberId 특정 Sorted set을 불러올 사용자 아이디
     * @return 최근 조회한 상품 아이디 목록 또는 빈 리스트
     */
    public List<Long> getRecentProductIds(Long memberId) {
        BoundZSetOperations<Long, Long> zSet = getOperations(memberId);
        Set<Long> members = zSet.reverseRange(0, 19); // 최근 본 순 (높은 score 순)
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(members);
        }
    }
}
