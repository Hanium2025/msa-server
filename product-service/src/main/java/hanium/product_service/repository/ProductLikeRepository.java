package hanium.product_service.repository;

import hanium.product_service.domain.ProductLike;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    @Modifying
    @Query(value = "INSERT INTO likes(product_id, member_id) VALUES(:productId, :memberId)", nativeQuery = true)
    void likeProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);

    @Modifying
    @Query(value = "DELETE FROM likes WHERE product_id = :productId AND member_id  = :memberId", nativeQuery = true)
    void unlikeProduct(@Param("memberId") Long memberId, @Param("productId") Long productId);

    @Query(value = """
            select distinct
                likedProduct.id as productId,
                likedProduct.title as title,
                likedProduct.price as price,
                singleImage.imageUrl as imageUrl
            from ProductLike productLike
                join productLike.product likedProduct
                left join ProductImage singleImage
                   on singleImage.product = likedProduct
                   and singleImage.id = (
                        select min(allImages.id)
                        from ProductImage allImages
                        where allImages.product = likedProduct
                   )
            where productLike.memberId = :memberId
            order by productLike.id desc
            """)
    List<ProductWithFirstImage> findLikedProductsWithFirstImage(@Param("memberId") Long memberId,
                                                                Pageable pageable);

    boolean existsByProductIdAndMemberId(Long productId, Long memberId);

    Long countByProductId(@Param("productId") Long productId);
}
