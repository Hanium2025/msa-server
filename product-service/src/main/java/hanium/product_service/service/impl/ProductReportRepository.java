package hanium.product_service.service.impl;

import hanium.product_service.domain.ProductReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReportRepository extends JpaRepository<ProductReport, Long> {
}
