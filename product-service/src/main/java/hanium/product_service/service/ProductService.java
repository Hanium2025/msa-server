package hanium.product_service.service;

import hanium.product_service.dto.request.DeleteImageRequestDTO;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductMainDTO;
import hanium.product_service.dto.response.ProductResponseDTO;

public interface ProductService {

    ProductMainDTO getProductMain(Long memberId);

    ProductResponseDTO registerProduct(RegisterProductRequestDTO dto);

    ProductResponseDTO getProductById(Long memberId, Long productId);

    ProductResponseDTO getProductAndViewLog(Long memberId, Long productId);

    ProductResponseDTO updateProduct(UpdateProductRequestDTO dto);

    void deleteProductById(Long productId, Long memberId);

    int deleteProductImage(DeleteImageRequestDTO dto);
}
