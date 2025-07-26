package hanium.product_service.repository;

import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProduct(Product product);
}
