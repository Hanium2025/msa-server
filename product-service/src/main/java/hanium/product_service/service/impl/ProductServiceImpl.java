package hanium.product_service.service.impl;

import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;

    @Override
    public ProductInfoResponseDTO registerProduct(RegisterProductRequestDTO dto) {

        Product product = Product.from(dto);
        productRepository.save(product);

        // 예시: 이미지 저장 (여러 장일 경우 반복문으로 처리 가능)
        ProductImage image = ProductImage.builder()
                .productId(product.getId())
                .imageUrl("https://your-storage.com/default.jpg") // 임시
                .build();
        imageRepository.save(image);

        return ProductInfoResponseDTO.from(product);
    }
}
