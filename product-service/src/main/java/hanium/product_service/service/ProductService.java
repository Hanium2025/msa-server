package hanium.product_service.service;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
public interface ProductService {
    ProductInfoResponseDTO registerProduct(RegisterProductRequestDTO dto);
}
