package hanium.product_service.service;

import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.DeleteImageRequestDTO;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO registerProduct(RegisterProductRequestDTO dto);

    ProductResponseDTO getProductById(Long id);

    void deleteProductById(Long productId, Long memberId);

    List<ProductImageDTO> getProductImages(Product product);

    int deleteProductImage(DeleteImageRequestDTO dto);

    ProductResponseDTO updateProduct(UpdateProductRequestDTO dto);
}
