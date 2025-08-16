package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import hanium.product_service.dto.request.DeleteImageRequestDTO;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductMainDTO;
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
     * 상품 메인 페이지 화면을 조회합니다.
     *
     * @param memberId 조회한 회원 id
     * @return 메인 페이지 결과 dto (최근 등록된 상품 + 최근 본 카테고리)
     */
    @Override
    public ProductMainDTO getProductMain(Long memberId) {
        //TODO: ProductService > getProductMain 구현
        return null;
    }

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
     * @param dto 상품 수정 정보 dto
     * @return 수정 결과 상품 정보 dto
     */
    @Override
    public ProductResponseDTO updateProduct(UpdateProductRequestDTO dto) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(dto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setTitle(dto.getTitle());
        product.setContent(dto.getContent());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        productRepository.save(product);
        for (String imageUrl : dto.getImageUrls()) {
            ProductImage productImage = ProductImage.of(product, imageUrl);
            productImageRepository.save(productImage);
        }
        return ProductResponseDTO.of(product, getProductImages(product));
    }

    /**
     * 상품 수정 시, 특정 상품 이미지를 삭제합니다.
     *
     * @param dto 삭제하지 않을 이미지 id 정보 dto
     * @return 특정 이미지 삭제 후, 상품 이미지 개수
     */
    @Override
    public int deleteProductImage(DeleteImageRequestDTO dto) {
        // 이미지 not found, 권한 없는 회원 예외
        Product product = productRepository.findByIdAndDeletedAtIsNull(dto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!dto.getMemberId().equals(product.getSellerId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
        // 삭제 처리
        for (ProductImage image : productImageRepository.findByProductAndDeletedAtIsNull(product)) {
            if (!dto.getLeftImageIds().contains(image.getId())) {
                image.setDeletedAt(LocalDateTime.now());
                productImageRepository.save(image);
            }
        }
        return dto.getLeftImageIds().size();
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
        for (ProductImage image : productImageRepository.findByProductAndDeletedAtIsNull(product)) {
            image.setDeletedAt(LocalDateTime.now());
            productImageRepository.save(image);
        }
    }

    /**
     * 상품 객체의 해당하는 이미지들을 가져와
     * ProductImageDTO 리스트 형태로 반환합니다.
     *
     * @param product 상품 객체
     * @return ProductImageDTO 리스트 (id, path)
     */
    @Override
    public List<ProductImageDTO> getProductImages(Product product) {
        return productImageRepository.findByProductAndDeletedAtIsNull(product)
                .stream().map(ProductImageDTO::from).toList();
    }
}