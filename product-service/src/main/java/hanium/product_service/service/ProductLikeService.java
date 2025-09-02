package hanium.product_service.service;

import hanium.product_service.dto.response.SimpleProductDTO;

import java.util.List;

public interface ProductLikeService {

    boolean likeProduct(Long memberId, Long productId);

    List<SimpleProductDTO> getLikedProducts(Long memberId, int page);
}
