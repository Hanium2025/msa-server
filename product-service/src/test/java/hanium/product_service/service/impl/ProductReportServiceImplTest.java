package hanium.product_service.service.impl;

import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductReport;
import hanium.product_service.dto.request.ReportProductRequestDTO;
import hanium.product_service.repository.ProductReportRepository;
import hanium.product_service.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@DisplayName("상품 신고 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductReportServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductReportRepository reportRepository;
    @Mock
    private EntityManager em;
    @InjectMocks
    private ProductReportServiceImpl sut;

    @Test
    @DisplayName("상품 신고")
    void reportProduct() {
        // given
        ReportProductRequestDTO req = mock(ReportProductRequestDTO.class);
        Product product = mock(Product.class);
        given(productRepository.findByIdAndDeletedAtIsNull(req.getProductId())).willReturn(Optional.of(product));
        given(em.getReference(Product.class, req.getProductId())).willReturn(product);

        // when
        sut.reportProduct(req);

        // then
        then(productRepository).should().findByIdAndDeletedAtIsNull(req.getProductId());
        then(reportRepository).should().save(any(ProductReport.class));
    }
}