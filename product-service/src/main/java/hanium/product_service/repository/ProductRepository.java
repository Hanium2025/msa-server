package hanium.product_service.repository;

import hanium.product_service.domain.Product;
import hanium.product_service.repository.projection.ProductIdCategory;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    @Query("""
            select p.id as id, p.category as category
            from Product p
            where p.id in :ids and p.deletedAt is null
            """)
    List<ProductIdCategory> findIdAndCategoryByIdIn(@Param("ids") Collection<Long> ids);

    @QueryHints(@QueryHint(name = AvailableHints.HINT_READ_ONLY, value = "true"))
    List<Product> findTop6ByOrderByCreatedAtDescIdDesc();
}