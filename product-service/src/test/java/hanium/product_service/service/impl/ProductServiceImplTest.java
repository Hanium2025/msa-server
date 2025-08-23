package hanium.product_service.service.impl;

import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.Status;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductRepository;

import hanium.product_service.repository.ProductSearchRepository;
import hanium.product_service.elasticsearch.ProductSearchElasticRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    ProductSearchElasticRepository productSearchElasticRepository;
    @Mock
    ProductSearchRepository productSearchRepository;

    @InjectMocks ProductServiceImpl productService;

    Product product;
    RegisterProductRequestDTO registerReq;
    UpdateProductRequestDTO updateReq;

    @BeforeEach
    void setUp() {
        product = Product.builder().sellerId(1L).title("title").content("content").price(1000L)
                .category(Category.BEAUTY).status(Status.SELLING).build();
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
        given(productRepository.save(any(Product.class))).willAnswer(inv -> {
            Product p = inv.getArgument(0);
            setField(p, "id", 1L);
            setField(p, "createdAt", LocalDateTime.now());
            return p;
        });

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
        // when
        Product found = productService.getProductById(1L);
        // then
        assertThat(found.getTitle()).isEqualTo(registerReq.getTitle());
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() {
        // given
        given(productRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(profileGrpcClient.getNicknameByMemberId(1L)).willReturn("피키");
        given(imageRepository.findByProductAndDeletedAtIsNull(product)).willReturn(new ArrayList<>());

        given(productRepository.save(any(Product.class))).willAnswer(inv -> {
            Product p = inv.getArgument(0);
            setField(p, "id", 1L);
            setField(p, "createdAt", LocalDateTime.now());
            return p;
        });

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
        given(productRepository.save(any(Product.class))).willAnswer(inv -> {
            Product p = inv.getArgument(0);
            setField(p, "id", 1L);
            setField(p, "createdAt", LocalDateTime.now());
            return p;
        });

        // when
        productService.deleteProductById(1L, 1L);
        // then
        verify(productRepository, times(1)).save(product);
    }

    private static void setField(Object target, String name, Object value) {
        Class<?> c = target.getClass();
        while (c != null) {
            try {
                var f = c.getDeclaredField(name);
                f.setAccessible(true);
                f.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass(); // BaseEntity 등으로 상승
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
