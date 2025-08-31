package hanium.product_service.repository;
import hanium.product_service.domain.ProductSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProductSearchRepository extends JpaRepository<ProductSearch, Long> {
    List<ProductSearch> findByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long memberId);

    Optional<ProductSearch> findByIdAndMemberIdAndDeletedAtIsNull(Long searchId, Long memberId);

    List<ProductSearch> findByMemberIdAndDeletedAtIsNull(Long memberId);

}
