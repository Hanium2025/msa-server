package hanium.product_service.service;

import hanium.product_service.dto.response.SimpleProductDTO;

import java.util.List;

public interface ProductUserService {

    List<String> getMainCategoryByMemberId(Long memberId);

    List<SimpleProductDTO> getMySellingProducts(Long memberId);

    List<SimpleProductDTO> getMyBuyingProducts(Long memberId);
}
