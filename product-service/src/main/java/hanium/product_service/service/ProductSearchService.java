package hanium.product_service.service;

import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;

public interface ProductSearchService {
    ProductSearchResponseDTO searchProduct(ProductSearchRequestDTO dto);

    ProductSearchResponseDTO searchProductReadOnly(ProductSearchRequestDTO dto);
}
