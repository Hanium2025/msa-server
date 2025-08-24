package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import hanium.product_service.domain.ProductSearch;
import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;
import hanium.product_service.elasticsearch.ProductDocument;
import hanium.product_service.elasticsearch.ProductSearchElasticRepository;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.ProductSearchRepository;
import hanium.product_service.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    private final ProductSearchElasticRepository productSearchElasticRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProfileGrpcClient profileGrpcClient;

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
    @Transactional(readOnly = true)
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
