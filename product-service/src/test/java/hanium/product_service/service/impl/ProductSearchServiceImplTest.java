package hanium.product_service.service.impl;

import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;
import hanium.product_service.dto.response.SimpleProductDTO;
import hanium.product_service.elasticsearch.ProductDocument;
import hanium.product_service.elasticsearch.ProductSearchElasticRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@DisplayName("상품 검색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductSearchServiceImplTest {

    @Mock
    private ProductSearchElasticRepository productSearchElasticRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchServiceImpl sut;

    @Test
    @DisplayName("상품을 키워드로 검색하고 최신순으로 정렬한다")
    void searchProductReadOnly_SortByRecent() {
        // given
        ProductSearchRequestDTO req = new ProductSearchRequestDTO(1L, "유모차", "recent", 0);
        Pageable pageable = PageRequest.of(req.getPage(), 20);

        ProductDocument doc1 = ProductDocument.builder().id(1L).title("유모차").build();
        ProductDocument doc2 = ProductDocument.builder().id(2L).title("유아 영어책").build();
        given(productSearchElasticRepository.findByTitle(req.getKeyword())).willReturn(List.of(doc1, doc2));

        List<Long> ids = List.of(1L, 2L);
        ProductWithFirstImage p1 = stubProduct(1L, "유모차", 100000L, "image_url");
        ProductWithFirstImage p2 = stubProduct(2L, "유아 영어책", 150000L, null);
        given(productRepository.findProductByIdsAndSortByRecent(ids, pageable)).willReturn(List.of(p1, p2));

        // when
        ProductSearchResponseDTO result = sut.searchProductReadOnly(req);

        // then
        assertThat(result.getProductList()).hasSize(2);
        SimpleProductDTO resultDto1 = result.getProductList().getFirst();
        assertThat(resultDto1.getProductId()).isEqualTo(1L);
        assertThat(resultDto1.getTitle()).isEqualTo("유모차");
        assertThat(resultDto1.getImageUrl()).isEqualTo("image_url");

        SimpleProductDTO resultDto2 = result.getProductList().get(1);
        assertThat(resultDto2.getProductId()).isEqualTo(2L);
        assertThat(resultDto2.getImageUrl()).isEqualTo("");

        then(productSearchElasticRepository).should().findByTitle(req.getKeyword());
        then(productRepository).should().findProductByIdsAndSortByRecent(ids, pageable);
    }

    @Test
    @DisplayName("상품을 키워드로 검색하고 인기순으로 정렬한다")
    void searchProductReadOnly_SortByLike() {
        // given
        ProductSearchRequestDTO req = new ProductSearchRequestDTO(1L, "의자", "like", 0);
        Pageable pageable = PageRequest.of(req.getPage(), 20);
        List<Long> ids = List.of(3L, 4L);

        ProductDocument doc1 = ProductDocument.builder().id(3L).title("유아 의자").build();
        ProductDocument doc2 = ProductDocument.builder().id(4L).title("유아용 의자").build();
        given(productSearchElasticRepository.findByTitle(req.getKeyword())).willReturn(List.of(doc1, doc2));

        ProductWithFirstImage p1 = stubProduct(3L, "유아 의자", 80000L, "image_url");
        ProductWithFirstImage p2 = stubProduct(4L, "유아용 의자", 200000L, "image_url");
        given(productRepository.findProductByIdsAndSortByLike(ids, pageable)).willReturn(List.of(p1, p2));

        // when
        ProductSearchResponseDTO result = sut.searchProductReadOnly(req);

        // then
        assertThat(result.getProductList()).hasSize(2);
        assertThat(result.getProductList().getFirst().getProductId()).isEqualTo(3L);
        then(productRepository).should().findProductByIdsAndSortByLike(ids, pageable);
    }

    private ProductWithFirstImage stubProduct(Long id, String title, Long price, String imageUrl) {
        return new ProductWithFirstImage() {
            @Override
            public Long getProductId() { return id; }
            @Override
            public String getTitle() { return title; }
            @Override
            public Long getPrice() { return price; }
            @Override
            public String getImageUrl() { return imageUrl; }
        };
    }
}