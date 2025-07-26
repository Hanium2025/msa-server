package hanium.product_service.service;

import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.SaveImageRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;

public interface ProductService {

    ProductInfoResponseDTO registerProduct(RegisterProductRequestDTO dto);

    void saveImage(SaveImageRequestDTO dto);

    ProductInfoResponseDTO getProductById(Long id);

    ProductInfoResponseDTO updateProduct(Long productId, Long memberId, UpdateProductRequestDTO dto);

    void deleteProductById(Long productId, Long memberId);
}
