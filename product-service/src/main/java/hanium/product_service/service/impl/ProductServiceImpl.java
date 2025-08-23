package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import hanium.product_service.domain.ProductSearch;
import hanium.product_service.dto.request.DeleteImageRequestDTO;
import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductMainDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;
import hanium.product_service.elasticsearch.ProductDocument;
import hanium.product_service.elasticsearch.ProductSearchElasticRepository;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.ProductSearchRepository;
import hanium.product_service.repository.RecentViewRepository;
import hanium.product_service.repository.projection.ProductIdCategory;
import hanium.product_service.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final RecentViewRepository recentViewRepository;
    private final ProfileGrpcClient profileGrpcClient;
    private final ProductSearchElasticRepository productSearchElasticRepository;
    private final ProductSearchRepository productSearchRepository;

    /**
     * 상품 메인 페이지 화면을 조회합니다.
     *
     * @param memberId 조회한 회원 id
     * @return 메인 페이지 결과 dto (최근 등록된 상품 + 최근 본 카테고리)
     */
    @Override
    public ProductMainDTO getProductMain(Long memberId) {
        return ProductMainDTO.builder()
                .products(getRecentProducts())
                .categories(getRecentCategories(memberId))
                .build();
    }

    /**
     * 새 상품을 등록합니다.
     *
     * @param dto 상품 등록 요청
     * @return 등록된 상품 객체의 id
     */
    @Override
    @Transactional
    public ProductResponseDTO registerProduct(RegisterProductRequestDTO dto) {
        Product product = Product.from(dto);
        productRepository.save(product);
        List<ProductImageDTO> images = new ArrayList<>();
        for (String imageUrl : dto.getImageUrls()) {
            ProductImage productImage = ProductImage.of(product, imageUrl);
            productImageRepository.save(productImage);
            images.add(ProductImageDTO.from(productImage));
        }
        String sellerNickname = profileGrpcClient.getNicknameByMemberId(product.getSellerId());

        // ProductDocument 저장
        ProductDocument document = ProductDocument.from(product);
        productSearchElasticRepository.save(document);

        return ProductResponseDTO.of(sellerNickname, product, images, true);
    }

    /**
     * id로 상품을 조회합니다.
     *
     * @param id 조회할 상품의 id
     * @return 상품 정보 dto
     */
    @Override
    public Product getProductById(Long id) {
        return productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    /**
     * 상품 id 및 사용자 id로 상품을 조회합니다.
     * 최근 조회된 상품/카테고리 구현을 포함합니다.
     *
     * @param memberId  조회한 사용자 아이디
     * @param productId 조회된 상품 아이디
     * @return 상품 정보 dto
     */
    @Override
    @Transactional
    public ProductResponseDTO getProductById(Long memberId, Long productId) {
        Product product = getProductById(productId);
        String sellerNickname = profileGrpcClient.getNicknameByMemberId(product.getSellerId());
        boolean isSeller = product.getSellerId().equals(memberId);
        try {
            recentViewRepository.add(memberId, productId);
            return ProductResponseDTO.of(sellerNickname, product, getProductImages(product), isSeller);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.RECENT_VIEW_SERVER_ERROR);
        }
    }

    /**
     * 상품을 수정합니다.
     *
     * @param dto 상품 수정 정보 dto
     * @return 수정 결과 상품 정보 dto
     */
    @Override
    @Transactional
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
        String sellerNickname = profileGrpcClient.getNicknameByMemberId(product.getSellerId());
        return ProductResponseDTO.of(sellerNickname, product, getProductImages(product), true);
    }

    /**
     * 상품 수정 시, 특정 상품 이미지를 삭제합니다.
     *
     * @param dto 삭제하지 않을 이미지 id 정보 dto
     * @return 특정 이미지 삭제 후, 상품 이미지 개수
     */
    @Override
    @Transactional
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
        // ProductDocument 수정
        ProductDocument document = ProductDocument.from(product);
        productSearchElasticRepository.save(document);

        return dto.getLeftImageIds().size();
    }

    /**
     * 상품을 삭제합니다.
     *
     * @param productId 삭제할 상품의 id
     * @param memberId  삭제 요청한 회원의 id
     */
    @Override
    @Transactional
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

        productSearchElasticRepository.deleteById(product.getId());
    }

    /**
     * 상품 객체의 해당하는 이미지들을 가져와
     * ProductImageDTO 리스트 형태로 반환합니다.
     *
     * @param product 상품 객체
     * @return ProductImageDTO 리스트 (id, path)
     */
    private List<ProductImageDTO> getProductImages(Product product) {
        return productImageRepository.findByProductAndDeletedAtIsNull(product)
                .stream().map(ProductImageDTO::from).toList();
    }

    /**
     * 최근 등록된 게시글 6개를 반환합니다.
     *
     * @return 최근 등록 게시글 리스트
     */
    public List<ProductMainDTO.MainProductsDTO> getRecentProducts() {
        List<Product> productList = productRepository.findTop6ByOrderByCreatedAtDescIdDesc();
        List<ProductMainDTO.MainProductsDTO> result = new ArrayList<>();
        for (Product product : productList) {
            List<ProductImage> imageUrls = productImageRepository.findByProductAndDeletedAtIsNull(product);
            String imageUrl;
            if (imageUrls.isEmpty()) {
                imageUrl = "";
            } else {
                imageUrl = imageUrls.getFirst().getImageUrl();
            }
            result.add(ProductMainDTO.MainProductsDTO.from(product, imageUrl));
        }
        if (result.size() > 6) {
            return result.subList(0, 5);
        } else {
            return result;
        }
    }

    /**
     * 최근 조회한 4개 카테고리 목록을 반환합니다.
     *
     * @param memberId 사용자 id
     * @return 최근 조회한 카테고리 목록, 4개, 형식: {이름, 아이콘 이미지 경로}
     */
    public List<ProductMainDTO.MainCategoriesDTO> getRecentCategories(Long memberId) {
        // 최근 조회한 상품 id 목록 조회
        List<Long> recentProductIds = recentViewRepository.getRecentProductIds(memberId);
        if (recentProductIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 최근 조회한 상품 id 목록으로 {id, Category} 목록 조회
        List<ProductIdCategory> rows = productRepository.findIdAndCategoryByIdIn(recentProductIds);
        Map<Long, Category> idToCategory = rows.stream().collect(
                Collectors.toMap(ProductIdCategory::getId, ProductIdCategory::getCategory));
        // {id, Category} 목록에서 중복 제거한 Category만 result로 담기
        boolean[] seen = new boolean[Category.values().length];
        List<ProductMainDTO.MainCategoriesDTO> result = new ArrayList<>();
        for (Long productId : recentProductIds) {
            Category c = idToCategory.get(productId);
            int idx = c.getIndex();
            if (!seen[idx]) {
                seen[idx] = true;
                result.add(getProductCategories(c));
            }
        }
        if (result.size() > 4) {
            return result.subList(0, 3);
        } else {
            return result;
        }
    }

    /**
     * 카테고리 Enum을 통해 한글 라벨 및 s3 저장경로를 포함해 반환합니다.
     *
     * @param category 카테고리
     * @return {name, imageUrl}
     */
    private ProductMainDTO.MainCategoriesDTO getProductCategories(Category category) {
        String baseUrl = "https://msa-image-bucket.s3.ap-northeast-2.amazonaws.com/product_category/";
        String imageUrl = baseUrl + category.name() + ".png";
        return ProductMainDTO.MainCategoriesDTO.builder()
                .name(category.getLabel())
                .imageUrl(imageUrl)
                .build();
    }

    /**
     * 상품 검색 기록을 저장합니다.
     * searchProductReadOnly(dto)를 호출하여 실제 검색 결과를 반환합니다.
     *
     * @param dto 상품 검색 요청 객체
     * @return searchProductReadOnly(dto)
     */
    @Override
    public ProductSearchResponseDTO searchProduct(ProductSearchRequestDTO dto) {
        productSearchRepository.save(ProductSearch.from(dto));
        return searchProductReadOnly(dto);
    }

    /**
     * Elastic에서 요청된 키워드로 목록을 검색합니다.
     *
     * @param dto 상품 검색 요청 객체
     * @return 검색 결과 반환
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ProductSearchResponseDTO searchProductReadOnly(ProductSearchRequestDTO dto) {

        List<ProductDocument> documents;
        try {
            documents = productSearchElasticRepository.findByTitle(dto.getKeyword());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ELASTICSEARCH_ERROR);
        }

        List<ProductResponseDTO> results = new ArrayList<>();
        for (ProductDocument document : documents) {
            Optional<Product> productOpt = productRepository.findById(document.getId());

            if (productOpt.isEmpty()) {
                log.warn("상품이 존재하지 않음. ID: {}", document.getId());
                continue;
            }

            Product product = productOpt.get();
            List<ProductImage> images = productImageRepository.findByProductAndDeletedAtIsNull(product);

            List<ProductImageDTO> imageDTOS = images.stream()
                    .map(ProductImageDTO::from)
                    .collect(Collectors.toList());

            String sellerNickname = profileGrpcClient.getNicknameByMemberId(product.getSellerId());

            results.add(ProductResponseDTO.of(sellerNickname, product, imageDTOS, true));
        }

        return ProductSearchResponseDTO.of(results);
    }
}