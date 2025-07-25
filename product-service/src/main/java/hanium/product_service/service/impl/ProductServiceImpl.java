package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * 새 상품을 등록합니다.
     *
     * @param dto 상품 등록 요청
     * @return 등록된 상품 정보 응답
     */
    @Override
    public ProductInfoResponseDTO registerProduct(RegisterProductRequestDTO dto) {
        Product product = Product.from(dto);
        productRepository.save(product);
        return ProductInfoResponseDTO.from(product);
    }

    /**
     * id로 상품을 조회합니다.
     *
     * @param id 조회할 상품의 id
     * @return 상품 정보 dto
     */
    @Override
    public ProductInfoResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductInfoResponseDTO.from(product);
    }
}
