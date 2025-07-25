package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
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

    /**
     * 상품을 수정합니다.
     *
     * @param postId   수정할 상품의 id
     * @param memberId 수정 요청한 회원의 id
     * @param dto      수정할 내용이 담긴 dto
     * @return 수정된 상품 정보 dto
     */
    @Override
    public ProductInfoResponseDTO updateProduct(Long postId, Long memberId, UpdateProductRequestDTO dto) {
        // 존재하지 않는 상품이거나 권한 없는 회원일 경우 예외처리
        Product product = productRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!memberId.equals(product.getSellerId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
        // 업데이트
        product.setTitle(dto.getTitle());
        product.setContent(dto.getContent());
        product.setPrice(dto.getPrice());
        product.setCategory(Category.valueOf(dto.getCategory()));
        productRepository.save(product);
        return ProductInfoResponseDTO.from(product);
    }
}