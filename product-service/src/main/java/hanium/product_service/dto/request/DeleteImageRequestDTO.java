package hanium.product_service.dto.request;

import hanium.common.proto.product.DeleteImageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteImageRequestDTO {

    private Long memberId;
    private Long productId;
    private List<Long> leftImageIds;

    public static DeleteImageRequestDTO from(DeleteImageRequest request) {
        return DeleteImageRequestDTO.builder()
                .memberId(request.getMemberId())
                .productId(request.getProductId())
                .leftImageIds(request.getLeftImageIdsList())
                .build();
    }
}
