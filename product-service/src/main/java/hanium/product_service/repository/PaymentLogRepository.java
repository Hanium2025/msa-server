package hanium.product_service.repository;

import hanium.product_service.domain.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

    Optional<PaymentLog> findByPaymentKey(String paymentKey);
}
