package hanium.product_service.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductSearchElasticRepository extends ElasticsearchRepository<ProductDocument, Long> {
    List<ProductDocument> findByTitle(String title);
}
