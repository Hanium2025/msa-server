package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Product;
import hanium.product_service.domain.ProductReport;
import hanium.product_service.dto.request.ReportProductRequestDTO;
import hanium.product_service.repository.ProductReportRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ProductReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReportServiceImpl implements ProductReportService {

    private final ProductRepository productRepository;
    private final ProductReportRepository reportRepository;

    @PersistenceContext
    private final EntityManager em;

    @Override
    @Transactional
    public void reportProduct(ReportProductRequestDTO dto) {
        productRepository.findByIdAndDeletedAtIsNull(dto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        Product productRef = em.getReference(Product.class, dto.getProductId());
        ProductReport report = ProductReport.of(productRef, dto);
        reportRepository.save(report);
    }
}
