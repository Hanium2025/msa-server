package hanium.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hanium.product_service.domain.Trade;

@Repository
public interface TradeRepository extends JpaRepository<Trade,Long> {
}
