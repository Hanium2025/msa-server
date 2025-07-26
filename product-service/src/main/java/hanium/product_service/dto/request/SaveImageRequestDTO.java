package hanium.product_service.dto.request;

import hanium.common.proto.product.SaveImageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveImageRequestDTO {
    private Long productId;
    private List<String> images;

    public static SaveImageRequestDTO from(SaveImageRequest request) {
        return SaveImageRequestDTO.builder()
                .productId(request.getProductId())
                .images(request.getImagePathList())
                .build();
    }
}
