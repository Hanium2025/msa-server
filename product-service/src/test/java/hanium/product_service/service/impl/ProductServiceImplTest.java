package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Category;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductImage;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductMainDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.SimpleProductDTO;
import hanium.product_service.elasticsearch.ProductSearchIndexer;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ProductImageRepository;
import hanium.product_service.repository.ProductReadRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.RecentViewRepository;
import hanium.product_service.repository.projection.ProductIdCategory;
import hanium.product_service.repository.projection.ProductWithFirstImage;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


@ActiveProfiles("test")
@DisplayName("상품 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private RecentViewRepository recentViewRepository;
    @Mock
    private ProfileGrpcClient profileGrpcClient;
    @Mock
    private ProductSearchIndexer productSearchIndexer;
    @Mock
    private ProductReadRepository productReadRepository;
    @Mock
    private EntityManager em;

    @InjectMocks
    private ProductServiceImpl sut;

    @Test
    @DisplayName("상품 홈: 상품, 카테고리 기록 없음")
    void getProductMain_noRecentProductsAndNoRecentViews() {
        // given
        given(productRepository.findRecentWithFirstImage(any())).willReturn(List.of());
        given(recentViewRepository.getRecentProductIds(anyLong())).willReturn(List.of());

        // when
        ProductMainDTO result = sut.getProductMain(1L);

        // then
        assertThat(result.getProducts()).isEmpty();
        assertThat(result.getCategories()).isEmpty();
        then(productRepository).should().findRecentWithFirstImage(any());
        then(recentViewRepository).should().getRecentProductIds(1L);
    }

    @Test
    @DisplayName("상품 홈")
    void getProductMain_whenRecentProductsExist_mapsViaSimpleProductDTO_andRecentCategoriesAreBuilt() {
        // given
        ProductWithFirstImage newProduct1 = mock(ProductWithFirstImage.class);
        ProductWithFirstImage newProduct2 = mock(ProductWithFirstImage.class);
        given(productRepository.findRecentWithFirstImage(any())).willReturn(List.of(newProduct1, newProduct2));

        SimpleProductDTO productDto1 = mock(SimpleProductDTO.class);
        SimpleProductDTO productDto2 = mock(SimpleProductDTO.class);

        long memberId = 1L;
        List<Long> recentViewedProductIds = List.of(10L, 11L, 10L); // 중복 확인용 10L 2개
        given(recentViewRepository.getRecentProductIds(memberId)).willReturn(recentViewedProductIds);

        // id+category 프로젝션
        Category[] categories = Category.values();
        Category c1 = categories[0];
        Category c2 = categories[1];
        ProductIdCategory ic1 = mock(ProductIdCategory.class);
        ProductIdCategory ic2 = mock(ProductIdCategory.class);
        given(ic1.getCategory()).willReturn(c1);
        given(ic2.getCategory()).willReturn(c2);
        given(productRepository.findIdAndCategoryByIdIn(recentViewedProductIds)).willReturn(List.of(ic1, ic2));

        try (MockedStatic<SimpleProductDTO> simpleProductDTOFrom = Mockito.mockStatic(SimpleProductDTO.class)) {
            // 프로젝션을 SimpleProductDTO로
            simpleProductDTOFrom.when(() -> SimpleProductDTO.from(newProduct1)).thenReturn(productDto1);
            simpleProductDTOFrom.when(() -> SimpleProductDTO.from(newProduct2)).thenReturn(productDto2);

            // when
            ProductMainDTO result = sut.getProductMain(memberId);

            // then (최근 등록 상품)
            assertThat(result.getProducts()).containsExactly(productDto1, productDto2);

            // then (최근 조회 카테고리)
            assertThat(result.getCategories()).hasSize(2);
            String base = "https://msa-image-bucket.s3.ap-northeast-2.amazonaws.com/product_category/";
            assertThat(result.getCategories().get(0).getName()).isEqualTo(c1.getLabel());
            assertThat(result.getCategories().get(0).getImageUrl()).isEqualTo(base + c1.name() + ".png");
            assertThat(result.getCategories().get(1).getName()).isEqualTo(c2.getLabel());
            assertThat(result.getCategories().get(1).getImageUrl()).isEqualTo(base + c2.name() + ".png");

            // then (메서드 호출됐는지)
            then(productRepository).should().findRecentWithFirstImage(any());
            then(recentViewRepository).should().getRecentProductIds(memberId);
            then(productRepository).should().findIdAndCategoryByIdIn(recentViewedProductIds);
        }
    }

    @Test
    @DisplayName("상품 등록")
    void registerProduct() {
        // given
        RegisterProductRequestDTO req = mock(RegisterProductRequestDTO.class);
        ProductResponseDTO res = mock(ProductResponseDTO.class);
        Product product = mock(Product.class);

        given(req.getImageUrls()).willReturn(List.of("u1", "u2"));
        given(req.getSellerId()).willReturn(12L);
        given(product.getId()).willReturn(34L);

        try (MockedStatic<Product> productFrom = Mockito.mockStatic(Product.class);
             MockedStatic<ProductImage> imageFrom = Mockito.mockStatic(ProductImage.class)) {

            productFrom.when(() -> Product.from(req)).thenReturn(product);
            imageFrom.when(() -> ProductImage.of(eq(product), anyString()))
                    .thenAnswer(inv -> mock(ProductImage.class));

            given(productReadRepository.findById(34L, 12L)).willReturn(Optional.of(res));
            given(profileGrpcClient.getNicknameByMemberId(res.getSellerId())).willReturn("피키");

            // when
            ProductResponseDTO result = sut.registerProduct(req);

            // then
            assertThat(result).isSameAs(res);
            then(productRepository).should().save(product);
            then(productImageRepository).should(times(2)).save(any(ProductImage.class));
            then(productSearchIndexer).should().index(product);
            then(profileGrpcClient).should().getNicknameByMemberId(res.getSellerId());
            then(res).should().updateSellerNickname("피키");
        }
    }

    @Test
    @DisplayName("상품 조회")
    void getProductById_returnsDto_andUpdatesSellerNickname() {
        // given
        Long memberId = 1L, productId = 2L;
        ProductResponseDTO dto = mock(ProductResponseDTO.class);
        given(productReadRepository.findById(productId, memberId)).willReturn(Optional.of(dto));
        given(profileGrpcClient.getNicknameByMemberId(dto.getSellerId())).willReturn("피키");

        // when
        ProductResponseDTO result = sut.getProductById(memberId, productId);

        // then
        assertThat(result).isSameAs(dto);
        then(dto).should().updateSellerNickname("피키");
    }

    @Test
    @DisplayName("상품 조회: id 없을 시 CustomException")
    void getProductById_notFound() {
        // given
        given(productReadRepository.findById(anyLong(), anyLong())).willReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> sut.getProductById(1L, 2L))
                .isInstanceOfSatisfying(CustomException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    @Test
    @DisplayName("상품 조회: 최근 조회 기록 저장")
    void getProductAndViewLog() {
        // given
        Long memberId = 1L, productId = 2L;
        ProductResponseDTO dto = mock(ProductResponseDTO.class);
        given(productReadRepository.findById(productId, memberId)).willReturn(Optional.of(dto));
        given(profileGrpcClient.getNicknameByMemberId(dto.getSellerId())).willReturn("피키");

        // when
        ProductResponseDTO result = sut.getProductAndViewLog(memberId, productId);

        // then
        assertThat(result).isSameAs(dto);
        then(recentViewRepository).should().add(memberId, productId);
    }

    @Test
    @DisplayName("상품 수정: 상품 찾을 수 없음")
    void updateProduct_whenNoRowsUpdated_throwsCustomException() {
        // given
        UpdateProductRequestDTO dto = UpdateProductRequestDTO.builder()
                .memberId(1L)
                .productId(1L)
                .title("t")
                .content("c")
                .price(1000L)
                .category(Category.BEAUTY)
                .imageUrls(List.of("u1", "u2"))
                .build();

        // when
        given(productRepository.updateFieldsById(
                anyLong(), any(), any(), any(), any())
        ).willReturn(0);

        // then
        assertThatThrownBy(() -> sut.updateProduct(dto))
                .isInstanceOfSatisfying(CustomException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() {
        // given
        UpdateProductRequestDTO dto = UpdateProductRequestDTO.builder()
                .memberId(1L)
                .productId(1L)
                .title("t")
                .content("c")
                .price(1000L)
                .category(Category.BEAUTY)
                .imageUrls(List.of("u1", "u2"))
                .build();

        given(productRepository.updateFieldsById(
                dto.getProductId(), dto.getTitle(), dto.getContent(), dto.getPrice(), dto.getCategory()
        )).willReturn(3);

        Product productRef = mock(Product.class);
        given(em.getReference(Product.class, 1L)).willReturn(productRef);

        ProductResponseDTO res = mock(ProductResponseDTO.class);
        given(productReadRepository.findById(1L, 1L)).willReturn(Optional.of(res));
        given(profileGrpcClient.getNicknameByMemberId(res.getSellerId())).willReturn("피키");

        try (MockedStatic<ProductImage> imageOf = Mockito.mockStatic(ProductImage.class)) {
            imageOf.when(() -> ProductImage.of(eq(productRef), anyString()))
                    .thenAnswer(inv -> mock(ProductImage.class));

            // when
            ProductResponseDTO result = sut.updateProduct(dto);

            // then
            assertThat(result).isSameAs(res);
            then(productImageRepository).should(times(2)).save(any(ProductImage.class));
            then(productSearchIndexer).should().index(productRef);
            then(res).should().updateSellerNickname("피키");
        }
    }

    @Test
    @DisplayName("상품 삭제: 상품 찾을 수 없음")
    void deleteProductById_notFound() {
        // given
        given(productRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.deleteProductById(1L, 1L))
                .isInstanceOfSatisfying(CustomException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND)
                );
    }

    @Test
    @DisplayName("상품 삭제: 권한 없음")
    void deleteProductById_whenNoPermission_throwsCustomException() {
        // given
        Product product = mock(Product.class);
        given(product.getSellerId()).willReturn(999L);
        given(productRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));

        // when, then
        assertThatThrownBy(() -> sut.deleteProductById(1L, 1L))
                .isInstanceOfSatisfying(CustomException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION)
                );
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProductById() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        given(product.getSellerId()).willReturn(2L);
        given(productRepository.findByIdAndDeletedAtIsNull(productId)).willReturn(Optional.of(product));

        ProductImage img1 = mock(ProductImage.class);
        ProductImage img2 = mock(ProductImage.class);
        given(productImageRepository.findByProductAndDeletedAtIsNull(product)).willReturn(List.of(img1, img2));

        // when
        sut.deleteProductById(productId, 2L);

        // then
        then(product).should().softDelete();
        then(img1).should().softDelete();
        then(img2).should().softDelete();
        then(productSearchIndexer).should().remove(productId);
    }
}