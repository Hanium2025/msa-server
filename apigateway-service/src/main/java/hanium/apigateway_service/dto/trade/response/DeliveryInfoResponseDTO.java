package hanium.apigateway_service.dto.trade.response;

import hanium.common.proto.product.GetDeliveryInfoResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryInfoResponseDTO {
    private String code;
    private String invoiceNo;
    private List<DeliveryStatusSummaryDTO> deliveryStatus;

    public static DeliveryInfoResponseDTO from(GetDeliveryInfoResponse response) {
        List<DeliveryStatusSummaryDTO> status = response.getDeliveryStatusSummaryList().stream()
                .map(DeliveryStatusSummaryDTO::from)
                .toList();
        return DeliveryInfoResponseDTO.builder()
                .code(response.getCode())
                .invoiceNo(response.getInvoiceNumber())
                .deliveryStatus(status)
                .build();
    }
}
