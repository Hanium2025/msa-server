package hanium.product_service.repository;

import hanium.product_service.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByTitle(String title);
}
