package hanium.apigateway_service.dto.trade.response;

import hanium.common.proto.product.DeliveryStatusSummary;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryStatusSummaryDTO {
    private String time;
    private String location;
    private String status;

    public static DeliveryStatusSummaryDTO from(DeliveryStatusSummary summary) {
        return DeliveryStatusSummaryDTO.builder()
                .time(summary.getTime())
                .location(summary.getLocation())
                .status(summary.getStatus())
                .build();
    }
}