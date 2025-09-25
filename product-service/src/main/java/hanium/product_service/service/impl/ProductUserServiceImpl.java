package hanium.product_service.service.impl;

import hanium.product_service.domain.Category;
import hanium.product_service.dto.response.SimpleProductDTO;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import hanium.product_service.service.ProductUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductUserServiceImpl implements ProductUserService {

    private final ProductRepository productRepository;

    // memberId별 판매 상품 기반한 주요 활동 카테고리 최대 2개 반환
    @Override
    public List<String> getMainCategoryByMemberId(Long memberId) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Category> productList = productRepository.findProductCategoryBySellerId(memberId, pageable);
        List<String> result = new ArrayList<>();
        for (Category item : productList) {
            String label = item.getLabel();
            if (!result.contains(label)) {
                result.add(label);
            }
            if (result.size() == 2) {
                return result;
            }
        }
        return result;
    }

    // memberId가 sellerId인 상품 목록 반환
    @Override
    public List<SimpleProductDTO> getMySellingProducts(Long memberId) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<ProductWithFirstImage> productList = productRepository.findProductBySellerId(memberId, pageable);
        return productList.getContent().stream().map(SimpleProductDTO::from).toList();
    }

    // trade에서 memberId가 sellerId인 상품 목록 반환
    @Override
    public List<SimpleProductDTO> getMyBuyingProducts(Long memberId) {
        Pageable pageable = PageRequest.of(0, 50);
        Page<ProductWithFirstImage> productList = productRepository.findProductByBuyerId(memberId, pageable);
        return productList.getContent().stream().map(SimpleProductDTO::from).toList();
    }
}
