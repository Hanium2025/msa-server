package hanium.product_service.dto.request;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.product.ReportProductRequest;
import hanium.product_service.domain.ReportReason;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportProductRequestDTO {
    private Long memberId;
    private Long productId;
    private ReportReason reason;
    private String details;

    public static ReportProductRequestDTO from(ReportProductRequest req) {
        try {
            ReportReason reason = ReportReason.valueOf(req.getReason());
            return ReportProductRequestDTO.builder()
                    .memberId(req.getMemberId())
                    .productId(req.getProductId())
                    .reason(reason)
                    .details(req.getDetails())
                    .build();
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.UNKNOWN_REPORT_REASON);
        }
    }
}
