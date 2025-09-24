package hanium.product_service.dto.request;

import hanium.common.proto.product.CreateWayBillRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateWayBillRequestDTO {
    private long tradeId;
    private long memberId;
    private String code;
    private String invoiceNo;

    public static CreateWayBillRequestDTO from(CreateWayBillRequest request) {
        return CreateWayBillRequestDTO.builder()
                .tradeId(request.getTradeId())
                .memberId(request.getMemberId())
                .code(request.getCode())
                .invoiceNo(request.getInvoiceNumber())
                .build();
    }

}
