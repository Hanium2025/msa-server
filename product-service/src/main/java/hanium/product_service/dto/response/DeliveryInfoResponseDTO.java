package hanium.product_service.dto.response;

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

    public static DeliveryInfoResponseDTO from(String code, String invoiceNo, List<DeliveryStatusSummaryDTO> deliveryStatus) {
        return DeliveryInfoResponseDTO.builder()
                .code(code)
                .invoiceNo(invoiceNo)
                .deliveryStatus(deliveryStatus)
                .build();
    }
}
