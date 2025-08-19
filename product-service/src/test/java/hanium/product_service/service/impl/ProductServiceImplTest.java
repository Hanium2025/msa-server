//package hanium.product_service.service.impl;
//
//import hanium.common.exception.CustomException;
//import hanium.product_service.domain.Category;
//import hanium.product_service.dto.request.RegisterProductRequestDTO;
//import hanium.product_service.dto.request.UpdateProductRequestDTO;
//import hanium.product_service.dto.response.ProductResponseDTO;
//import hanium.product_service.repository.ProductImageRepository;
//import hanium.product_service.repository.ProductRepository;
//import hanium.product_service.repository.RecentViewRepository;
//import hanium.product_service.service.ProductService;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//
//@SpringBootTest
//@Transactional
//@ActiveProfiles("test")
//class ProductServiceImplTest {
//
//    private final ProductService productService;
//    private final ProductRepository productRepository;
//    private final ProductImageRepository imageRepository;
//    private final RecentViewRepository recentViewRepository;
//    private RegisterProductRequestDTO registerDTO1, registerDTO2;
//
//    @Autowired
//    public ProductServiceImplTest(ProductService productService, ProductRepository productRepository,
//                                  ProductImageRepository imageRepository, RecentViewRepository recentViewRepository) {
//        this.productService = productService;
//        this.productRepository = productRepository;
//        this.imageRepository = imageRepository;
//        this.recentViewRepository = recentViewRepository;
//    }
//
//    @BeforeEach
//    void setUp() {
//        List<String> imageUrls = new ArrayList<>(Arrays.asList("url1", "url2", "url3"));
//        registerDTO1 = RegisterProductRequestDTO.builder()
//                .sellerId(1L).title("옷").content("내용").price(10000L)
//                .category(Category.valueOf("CLOTHES")).imageUrls(imageUrls).build();
//        registerDTO2 = RegisterProductRequestDTO.builder()
//                .sellerId(1L).title("화장품").content("내용").price(20000L)
//                .category(Category.valueOf("BEAUTY")).imageUrls(imageUrls).build();
//        productRepository.deleteAll();
//        imageRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("상품 등록")
//    void registerProduct() {
//        // given: request
//        // when
//        ProductResponseDTO result = productService.registerProduct(registerDTO1);
//        // then
//        assertThat(result.getId()).isNotNull();
//        assertThat(result.getTitle()).isEqualTo(registerDTO1.getTitle());
//    }
//
//    @Test
//    @DisplayName("상품 조회")
//    void getProductById() {
//        // given
//        ProductResponseDTO product1 = productService.registerProduct(registerDTO1);
//        // when
//        ProductResponseDTO found = productService.getProductById(product1.getId());
//        // then
//        assertThat(found.getId()).isEqualTo(product1.getId());
//        CustomException e = assertThrows(CustomException.class, () -> productService.getProductById(2L));
//        assertThat(e.getErrorCode().name()).isEqualTo("PRODUCT_NOT_FOUND");
//    }
//
//    @Test
//    @DisplayName("상품 조회 - Redis 기록 업데이트 확인")
//    void getProductAndCheckRedis() {
//        // given
//        ProductResponseDTO product = productService.registerProduct(registerDTO1);
//        productService.getProductById(1L, product.getId());
//        // when
//        List<Long> viewedProductIds = recentViewRepository.getRecentProductIds(1L);
//        // then
//        assertThat(viewedProductIds).contains(product.getId());
//    }
//
//    @Test
//    @DisplayName("상품 조회 - 최근 조회 순서 업데이트 확인")
//    void getRecentCategories() {
//        // given
//        ProductResponseDTO product1 = productService.registerProduct(registerDTO1);
//        ProductResponseDTO product2 = productService.registerProduct(registerDTO2);
//        // when - 1
//        productService.getProductById(1L, product1.getId());
//        List<Long> viewedProductIds = recentViewRepository.getRecentProductIds(1L);
//        // then - 1
//        assertThat(viewedProductIds.getFirst()).isEqualTo(product1.getId());
//        // when - 1
//        productService.getProductById(1L, product2.getId());
//        viewedProductIds = recentViewRepository.getRecentProductIds(1L);
//        // then - 1
//        assertThat(viewedProductIds.getFirst()).isEqualTo(product2.getId());
//    }
//
//    @Test
//    @DisplayName("상품 수정")
//    void updateProduct() {
//        // given
//        ProductResponseDTO product = productService.registerProduct(registerDTO1);
//        UpdateProductRequestDTO updateDTO = UpdateProductRequestDTO.builder()
//                .productId(product.getId()).title("상품명").content("내용").price(10000L)
//                .category(Category.valueOf("CLOTHES")).imageUrls(new ArrayList<>()).build();
//        // when
//        ProductResponseDTO updated = productService.updateProduct(updateDTO);
//        ProductResponseDTO found = productService.getProductById(updated.getId());
//        // then
//        assertThat(found.getTitle()).isEqualTo(updated.getTitle());
//    }
//
//    @Test
//    @DisplayName("상품 삭제")
//    void deleteProductById() {
//        // given
//        ProductResponseDTO product = productService.registerProduct(registerDTO1);
//        // when
//        productService.deleteProductById(product.getId(), product.getSellerId());
//        // then
//        assertThat(productRepository.findById(product.getId()).get().isSoftDeleted()).isTrue();
//        assertThat(assertThrows(CustomException.class, () -> productService.getProductById(2L))
//                .getErrorCode().name()).isEqualTo("PRODUCT_NOT_FOUND");
//    }
//}