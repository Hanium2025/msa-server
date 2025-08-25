package hanium.product_service.repository;

import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.repository.projection.ProductCoreProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ProductReadRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<ProductResponseDTO> findById(Long productId, Long memberId) {

        // 상품 + 좋아요
        ProductCoreProjection core;
        try {
            core = em.createQuery("""
                                    select new hanium.product_service.repository.projection.ProductCoreProjection(
                                            p.id, p.title, p.content, p.price, p.sellerId,
                                            p.status, p.category,
                                            (select
                                            case when count(pl) > 0 then true else false end
                                                from ProductLike pl
                                               where pl.product.id = :productId
                                                 and pl.memberId   = :memberId),
                                             case when p.sellerId = :memberId then true else false end
                                    )
                                    from Product p
                                    where p.id = :productId and p.deletedAt is null
                                    """,
                            ProductCoreProjection.class
                    )
                    .setParameter("productId", productId)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return Optional.empty();
        }

        log.info("✅ Product core checked: {}", core.getTitle());

        // 상품 + 이미지목록
        List<ProductImageDTO> images = em.createQuery("""
                                select new hanium.product_service.dto.response.ProductImageDTO(i.id, i.imageUrl)
                                from ProductImage i
                                where i.deletedAt is null and i.product.id = :productId
                                order by i.id asc
                                """,
                        ProductImageDTO.class)
                .setParameter("productId", productId)
                .getResultList();

        log.info("✅ Product image checked: {}", images);

        // 응답 반환
        ProductResponseDTO response = ProductResponseDTO.builder()
                .productId(core.getId())
                .sellerId(core.getSellerId())
                .title(core.getTitle())
                .content(core.getContent())
                .price(core.getPrice())
                .status(core.getStatus().getLabel())
                .category(core.getCategory().getLabel())
                .isLiked(core.isLiked())
                .isSeller(core.isSeller())
                .images(images)
                .build();

        log.info("✅ ProductResponseDTO built: {}", response);
        return Optional.of(response);
    }
}
