package hanium.product_service.elasticsearch;

import hanium.product_service.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchIndexer {

    private final ProductSearchElasticRepository productSearchElasticRepository;


    public void index(Product product) {

        ProductDocument doc = ProductDocument.from(product);
        productSearchElasticRepository.save(doc);
    }

    public void remove(Long productId) {
        productSearchElasticRepository.deleteById(productId);
    }
}

