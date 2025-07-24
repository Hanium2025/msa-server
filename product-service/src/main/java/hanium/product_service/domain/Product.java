package hanium.product_service.domain;

import hanium.product_service.dto.request.RegisterProductRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE PRODUCT SET PRODUCT.DELETED_AT = CURRENT_TIMESTAMP WHERE PRODUCT.ID = ?")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(length = 1000)
    private String content;

    @Column
    private Long price;

    @Column
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static Product from(RegisterProductRequestDTO dto) {
        return Product.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .sellerId(dto.getSellerId())
                .category(dto.getCategory())
                .status(Status.SELLING)
                .build();
    }
}
