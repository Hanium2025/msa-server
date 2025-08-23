package hanium.product_service.elasticsearch;

import hanium.product_service.domain.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

@Document(indexName = "product_search", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate createdAt;

    public static ProductDocument from(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.id = product.getId();
        doc.title = product.getTitle();
        doc.createdAt = product.getCreatedAt().toLocalDate();;
        return doc;
    }
}

