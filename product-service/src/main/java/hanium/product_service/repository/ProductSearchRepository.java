package hanium.product_service.repository;
import hanium.product_service.domain.ProductSearch;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductSearchRepository extends JpaRepository<ProductSearch, Long> {
}
