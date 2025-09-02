package hanium.product_service.domain;

import hanium.product_service.dto.request.ProductSearchRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column(length = 30)
    private String keyword;

    public static ProductSearch from(ProductSearchRequestDTO dto) {
        return ProductSearch.builder()
                .memberId(dto.getMemberId())
                .keyword(dto.getKeyword())
                .build();

    }
}
