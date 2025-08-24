package hanium.product_service.service.impl;

import hanium.product_service.dto.response.SimpleProductDTO;
import hanium.product_service.repository.ProductLikeRepository;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import hanium.product_service.service.ProductLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductLikeServiceImpl implements ProductLikeService {

    private final ProductLikeRepository likeRepository;

    /**
     * 상품을 찜 혹은 찜 취소합니다.
     *
     * @param memberId  요청한 사용자 id
     * @param productId 대상 상품 id
     * @return 상품 찜이 취소된 것인지 여부
     */
    @Override
    @Transactional
    public boolean likeProduct(Long memberId, Long productId) {
        if (likeRepository.existsByProductIdAndMemberId(memberId, productId)) {
            likeRepository.unlikeProduct(memberId, productId);
            return true;
        }
        likeRepository.likeProduct(memberId, productId);
        return false;
    }

    /**
     * 찜한 상품을 20개씩 페이지네이션해 반환합니다.
     *
     * @param memberId 요청한 사용자 id
     * @param page     요청한 페이지 number
     * @return {id, title, price, imageUrl}로 이루어진 상품 응답 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<SimpleProductDTO> getLikedProducts(Long memberId, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        List<ProductWithFirstImage> products =
                likeRepository.findLikedProductsWithFirstImage(memberId, pageable);
        // imageUrl이 null일 경우 빈 문자열로
        return products.stream()
                .map(p -> SimpleProductDTO.builder()
                        .productId(p.getProductId())
                        .title(p.getTitle())
                        .price(p.getPrice())
                        .imageUrl(p.getImageUrl() == null ? "" : p.getImageUrl())
                        .build())
                .toList();
    }
}
