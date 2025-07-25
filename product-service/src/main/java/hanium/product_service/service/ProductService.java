package hanium.product_service.service;

import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;

public interface ProductService {

    ProductInfoResponseDTO registerProduct(RegisterProductRequestDTO dto);

    ProductInfoResponseDTO getProductById(Long id);

    ProductInfoResponseDTO updateProduct(Long postId, Long memberId, UpdateProductRequestDTO dto);
}
