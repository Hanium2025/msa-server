package hanium.product_service.service.impl;

import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@DisplayName("ProductServiceImpl 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    ProductImageRepository imageRepository;
    @Mock
    ProfileGrpcClient profileGrpcClient;
    @InjectMocks
    ProductServiceImpl productService;

    Product product;
    RegisterProductRequestDTO registerReq;
    UpdateProductRequestDTO updateReq;

    @BeforeEach
    void setUp() {
        product = Product.builder().sellerId(1L).title("title").content("content").price(1000L)
                .category(Category.BEAUTY).build();
        registerReq = RegisterProductRequestDTO.builder()
                .sellerId(1L).title("title").content("content").price(1000L)
                .category(Category.BEAUTY).imageUrls(new ArrayList<>()).build();
        updateReq = UpdateProductRequestDTO.builder().productId(1L).title("title2").content("content2")
                .price(2000L).category(Category.BOOK).imageUrls(new ArrayList<>()).build();
    }

    @Test
    @DisplayName("상품 등록")
    void registerProduct() {
        // given
        given(profileGrpcClient.getNicknameByMemberId(1L)).willReturn("피키");
        // when
        ProductResponseDTO result = productService.registerProduct(registerReq);
        // then
        assertThat(result.getTitle()).isEqualTo(registerReq.getTitle());
        assertThat(result.getSellerNickname()).isEqualTo("피키");
    }

    @Test
    @DisplayName("상품 조회")
    void getProduct() {
        // given
        given(productRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(profileGrpcClient.getNicknameByMemberId(1L)).willReturn("피키");
        given(imageRepository.findByProductAndDeletedAtIsNull(product)).willReturn(new ArrayList<>());
        // when
        ProductResponseDTO found = productService.getProductById(1L);
        // then
        assertThat(found.getTitle()).isEqualTo(registerReq.getTitle());
        assertThat(found.getSellerNickname()).isEqualTo("피키");
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() {
        // given
        given(productRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(profileGrpcClient.getNicknameByMemberId(1L)).willReturn("피키");
        given(imageRepository.findByProductAndDeletedAtIsNull(product)).willReturn(new ArrayList<>());
        // when
        ProductResponseDTO updated = productService.updateProduct(updateReq);
        // then
        assertThat(updated.getTitle()).isEqualTo(updateReq.getTitle());
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() {
        // given
        given(productRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(imageRepository.findByProductAndDeletedAtIsNull(product)).willReturn(new ArrayList<>());
        // when
        productService.deleteProductById(1L, 1L);
        // then
        verify(productRepository, times(1)).save(product);
    }
}