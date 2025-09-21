package hanium.product_service.repository;

import hanium.product_service.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByTradeId(Long tradeId);
}
