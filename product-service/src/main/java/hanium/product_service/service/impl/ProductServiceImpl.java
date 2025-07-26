package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * 새 상품을 등록합니다.
     *
     * @param dto 상품 등록 요청
     * @return 등록된 상품 객체의 id
     */
    @Override
    public ProductResponseDTO registerProduct(RegisterProductRequestDTO dto) {
        Product product = Product.from(dto);
        productRepository.save(product);
        List<ProductImageDTO> images = new ArrayList<>();
        for (String imageUrl : dto.getImageUrls()) {
            ProductImage productImage = ProductImage.of(product, imageUrl);
            productImageRepository.save(productImage);
            images.add(ProductImageDTO.from(productImage));
        }
        return ProductResponseDTO.of(product, images);
    }

    /**
     * id로 상품을 조회합니다.
     *
     * @param id 조회할 상품의 id
     * @return 상품 정보 dto
     */
    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponseDTO.of(product, getProductImages(product));
    }

    /**
     * 상품을 수정합니다.
     *
     * @param dto 수정할 내용이 담긴 dto
     * @return 수정된 상품 정보 dto
     */
    @Override
    public ProductResponseDTO updateProduct(UpdateProductRequestDTO dto) {
        // 존재하지 않는 상품이거나 권한 없는 회원일 경우 예외처리
        Product product = productRepository.findByIdAndDeletedAtIsNull(dto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!dto.getMemberId().equals(product.getSellerId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
        // 업데이트
        product.setTitle(dto.getTitle());
        product.setContent(dto.getContent());
        product.setPrice(dto.getPrice());
        product.setCategory(Category.valueOf(dto.getCategory()));
        productRepository.save(product);
        return ProductResponseDTO.of(product, getProductImages(product));
    }

    /**
     * 상품을 삭제합니다.
     *
     * @param productId 삭제할 상품의 id
     * @param memberId  삭제 요청한 회원의 id
     */
    @Override
    public void deleteProductById(Long productId, Long memberId) {
        // 존재하지 않는 상품이거나 권한 없는 회원일 경우 예외처리
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!memberId.equals(product.getSellerId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
        // 삭제 처리
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
        for (ProductImage image : productImageRepository.findByProduct(product)) {
            image.setDeletedAt(LocalDateTime.now());
            productImageRepository.save(image);
        }
    }

    @Override
    public List<ProductImageDTO> getProductImages(Product product) {
        return productImageRepository.findByProduct(product).stream().map(ProductImageDTO::from).toList();
    }
}