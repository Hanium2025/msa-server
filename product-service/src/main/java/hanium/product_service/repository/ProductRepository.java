package hanium.product_service.repository;

import hanium.product_service.domain.Product;
import hanium.product_service.repository.projection.ProductIdCategory;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Query("""
            select
                product.id as productId,
                product.title as title,
                product.price as price,
                singleImage.imageUrl as imageUrl
            from Product product
            left join ProductImage singleImage
                on singleImage.product = product
               and singleImage.deletedAt is null
               and singleImage.id = (
                    select min(allImages.id)
                    from ProductImage allImages
                    where allImages.product = product
                      and allImages.deletedAt is null
               )
            where product.deletedAt is null
            order by product.createdAt desc, product.id desc
            """)
    List<ProductWithFirstImage> findRecentWithFirstImage(Pageable pageable);

    @Query("""
            select
                product.id as productId,
                product.title as title,
                product.price as price,
                singleImage.imageUrl as imageUrl
            from Product product
            left join ProductImage singleImage
                on singleImage.product = product
               and singleImage.deletedAt is null
               and singleImage.id = (
                    select min(allImages.id)
                    from ProductImage allImages
                    where allImages.product = product
                      and allImages.deletedAt is null
               )
            where product.id in :ids and product.deletedAt is null
            """)
    List<ProductWithFirstImage> findProductWithFirstImageByIds(@Param("ids") Collection<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product p
            set p.title   = :title,
                p.content = :content,
                p.price   = :price,
                p.category = :category,
                p.updatedAt = now()
            where p.id      = :productId
            """)
    int updateFieldsById(@Param("productId") Long productId,
                         @Param("title") String title,
                         @Param("content") String content,
                         @Param("price") Long price,
                         @Param("category") String category);
}