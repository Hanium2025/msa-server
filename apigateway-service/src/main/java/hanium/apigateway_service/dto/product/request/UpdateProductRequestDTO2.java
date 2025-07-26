package hanium.apigateway_service.dto.product.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDTO2 {
    private String title;
    private String content;
    private Long price;
    private String category;
    private List<Long> leftImageIds; // 삭제하지 않고 유지되길 원하는 이미지의 id들
}
