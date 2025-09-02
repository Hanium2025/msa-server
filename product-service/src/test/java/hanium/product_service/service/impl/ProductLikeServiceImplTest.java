package hanium.product_service.service.impl;

import hanium.product_service.dto.response.SimpleProductDTO;
import hanium.product_service.repository.ProductLikeRepository;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@DisplayName("상품 관심 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductLikeServiceImplTest {

    @Mock
    ProductLikeRepository likeRepository;
    @InjectMocks
    ProductLikeServiceImpl likeService;

    @Test
    @DisplayName("관심 안 누른 상품이면 관심 등록된다")
    void likeProduct() {
        // given
        given(likeRepository.existsByProductIdAndMemberId(1L, 1L)).willReturn(false);
        // when
        boolean result = likeService.likeProduct(1L, 1L);
        // then
        assertThat(result).isFalse();
        verify(likeRepository, times(1)).likeProduct(1L, 1L);
    }

    @Test
    @DisplayName("관심 누른 상품이면 관심 취소된다")
    void unlikeProduct() {
        // given
        given(likeRepository.existsByProductIdAndMemberId(1L, 1L)).willReturn(true);
        // when
        boolean result = likeService.likeProduct(1L, 1L);
        // then
        assertThat(result).isTrue();
        verify(likeRepository, times(1)).unlikeProduct(1L, 1L);
    }

    @Test
    @DisplayName("관심 상품 목록을 조회한다")
    void getLikedProducts() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        ProductWithFirstImage p1 = stubProduct(1L, "A", 1000L, "url");
        ProductWithFirstImage p2 = stubProduct(2L, "B", 2000L, null);
        given(likeRepository.findLikedProductsWithFirstImage(1L, pageable)).willReturn(List.of(p1, p2));
        // when
        List<SimpleProductDTO> result = likeService.getLikedProducts(1L, 0);
        // then
        assertThat(result).hasSize(2);
        SimpleProductDTO dto2 = result.get(1);
        assertThat(dto2.getProductId()).isEqualTo(2L);
        assertThat(dto2.getImageUrl()).isEqualTo("");
    }

    private ProductWithFirstImage stubProduct(Long id, String title, Long price, String imageUrl) {
        return new ProductWithFirstImage() {
            @Override
            public Long getProductId() {
                return id;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public Long getPrice() {
                return price;
            }

            @Override
            public String getImageUrl() {
                return imageUrl;
            }
        };
    }
}