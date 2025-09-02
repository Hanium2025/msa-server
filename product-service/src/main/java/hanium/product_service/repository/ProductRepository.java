package hanium.product_service.repository;

import hanium.product_service.domain.Category;
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
            where p.id in :ids
              and p.deletedAt is null
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
                         @Param("category") Category category);

    @Query(
            value = """
                    select p.id as productId, p.title as title, p.price as price, i.image_url as imageUrl
                    from product p
                    /* 각 상품의 첫 번째 이미지 가져오기 */
                    left join (
                        select product_id, min(id) as min_image_id
                        from product_image
                        where deleted_at is null
                        group by product_id
                    ) min_image_table on min_image_table.product_id = p.id
                    left join product_image i on i.id = min_image_table.min_image_id
                    where p.category = :category and p.deleted_at is null
                    order by p.created_at desc, p.id desc
                    """,
            countQuery = """
                    select count(*)
                    from product
                    where p.category = :category
                    and p.deleted_at is null
                    """,
            nativeQuery = true)
    List<ProductWithFirstImage> findProductByCategoryAndSortByRecent(@Param("category") String category,
                                                                     Pageable pageable);

    @Query(
            value = """
                    select p.id as productId, p.title as title, p.price as price, fi.image_url as imageUrl
                    from product p
                    /* 상품별 ProductLike 카운트한 lc 테이블 조인 */
                    left join (
                        select pl.product_id, count(*) as like_count
                        from product_like pl
                        group by pl.product_id
                    ) lc on lc.product_id = p.id
                    /* fi 테이블에서 실제 가져온 상품 p에 맞는 이미지만 선택 (조인) */
                    left join (
                        select x.product_id, pi.image_url
                        /* 상품별 첫 번째 ProductImage만 가져온 x 테이블로 */
                        /* pi 테이블에서 첫 번째 이미지 url만 선택 -> fi 테이블 */
                        from (
                            select product_id, MIN(id) AS first_image
                            from product_image
                            group by product_id
                        ) x
                        join product_image pi
                          on pi.id = x.first_image
                    ) fi on fi.product_id = p.id
                    where p.category = :category
                      and p.deleted_at is null
                      and p.created_at >= NOW() - interval 1 year
                    order by coalesce(lc.like_count, 0) desc, p.id desc
                    """,
            countQuery = """
                    select count(*)
                    from product p
                    where p.category = :category
                      and p.deleted_at is null
                      and p.created_at >= NOW() - interval 1 year
                    """,
            nativeQuery = true
    )
    List<ProductWithFirstImage> findProductByCategoryAndSortByLike(@Param("category") String category,
                                                                   Pageable pageable);
}