package hanium.product_service.mapper;

import hanium.common.proto.product.*;
import hanium.product_service.dto.response.DeliveryInfoResponseDTO;
import hanium.product_service.dto.response.DeliveryStatusSummaryDTO;
import hanium.product_service.dto.response.TradeReviewPageDTO;

import java.util.stream.Collectors;

public class TradeGrpcMapper {

    public static GetTradeReviewPageResponse toGetTradeReviewPageResponseGrpc(TradeReviewPageDTO dto) {
        return GetTradeReviewPageResponse.newBuilder()
                .setTitle(dto.getTitle())
                .setNickname(dto.getNickname())
                .build();
    }

    public static GetDeliveryInfoResponse toGetDeliveryInfoResponseGrpc(DeliveryInfoResponseDTO dto) {
        return GetDeliveryInfoResponse.newBuilder()
                .setCode(dto.getCode())
                .setInvoiceNumber(dto.getInvoiceNo())
                .addAllDeliveryStatusSummary(dto.getDeliveryStatus().stream()
                        .map(TradeGrpcMapper::toDeliveryStatusSummaryGrpc)
                        .collect(Collectors.toList()))
                .build();
    }

    public static DeliveryStatusSummary toDeliveryStatusSummaryGrpc(DeliveryStatusSummaryDTO dto) {
        return DeliveryStatusSummary.newBuilder()
                .setTime(dto.getTime())
                .setLocation(dto.getLocation())
                .setStatus(dto.getStatus())
                .build();
    }
}
