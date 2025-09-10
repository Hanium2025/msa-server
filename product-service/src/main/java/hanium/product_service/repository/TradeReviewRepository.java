package hanium.product_service.repository;

import hanium.product_service.domain.TradeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeReviewRepository extends JpaRepository<TradeReview, Long> {
    boolean existsByTradeIdAndMemberId(Long tradeId, Long memberId);
}
