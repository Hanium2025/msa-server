package hanium.product_service.service;

import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.DeleteImageRequestDTO;
import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductMainDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;

import java.util.List;

public interface ProductService {

    ProductMainDTO getProductMain(Long memberId);

    List<ProductMainDTO.MainProductsDTO> getRecentProducts();

    List<ProductMainDTO.MainCategoriesDTO> getRecentCategories(Long memberId);

    ProductResponseDTO registerProduct(RegisterProductRequestDTO dto);

    Product getProductById(Long id);

    ProductResponseDTO getProductById(Long memberId, Long productId);

    void deleteProductById(Long productId, Long memberId);

    int deleteProductImage(DeleteImageRequestDTO dto);

    ProductResponseDTO updateProduct(UpdateProductRequestDTO dto);

    ProductSearchResponseDTO searchProduct(ProductSearchRequestDTO dto);

    ProductSearchResponseDTO searchProductReadOnly(ProductSearchRequestDTO dto);

}
