package hanium.product_service.elasticsearch;

import hanium.product_service.domain.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "product_search")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDocument {
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate createdAt;

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .id(product.getId())
                .title(product.getTitle())
                .createdAt(product.getCreatedAt().toLocalDate())
                .build();
    }
}

