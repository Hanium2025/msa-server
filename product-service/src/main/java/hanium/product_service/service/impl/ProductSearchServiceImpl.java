package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.ProductSearch;
import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;
import hanium.product_service.dto.response.SimpleProductDTO;
import hanium.product_service.elasticsearch.ProductDocument;
import hanium.product_service.elasticsearch.ProductSearchElasticRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.ProductSearchRepository;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import hanium.product_service.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    private final ProductSearchElasticRepository productSearchElasticRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 검색 기록을 저장합니다.
     * searchProductReadOnly(dto)를 호출하여 실제 검색 결과를 반환합니다.
     *
     * @param dto 상품 검색 요청 객체
     * @return searchProductReadOnly(dto)
     */
    @Override
    @Transactional
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
    @Transactional
    public ProductSearchResponseDTO searchProductReadOnly(ProductSearchRequestDTO dto) {

        log.info("✅ Searching for products with keyword: {}", dto.getKeyword());
        List<ProductDocument> documents;
        try {
            documents = productSearchElasticRepository.findByTitle(dto.getKeyword());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ELASTICSEARCH_ERROR);
        }

        List<Long> ids = documents.stream()
                .map(ProductDocument::getId)
                .toList();
        List<ProductWithFirstImage> products = productRepository.findProductWithFirstImageByIds(ids);

        return ProductSearchResponseDTO.from(products.stream().map(SimpleProductDTO::from).toList());
    }
}
