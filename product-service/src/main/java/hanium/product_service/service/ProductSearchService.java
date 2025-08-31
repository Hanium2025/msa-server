package hanium.product_service.service;

import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.response.ProductSearchHistoryDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;

import java.util.List;

public interface ProductSearchService {
    
    ProductSearchResponseDTO searchProduct(ProductSearchRequestDTO dto);

    ProductSearchResponseDTO searchProductReadOnly(ProductSearchRequestDTO dto);

    List<ProductSearchHistoryDTO> productSearchHistory(Long memberId);

    void deleteProductSearchHistory(Long memberId, Long historyId);

    void deleteAllProductSearchHistory(Long memberId);
}
