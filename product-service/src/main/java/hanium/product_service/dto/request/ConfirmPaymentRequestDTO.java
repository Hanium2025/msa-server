package hanium.product_service.dto.request;

import hanium.common.proto.product.ConfirmPaymentRequest;

public record ConfirmPaymentRequestDTO(
        Long tradeId,
        String paymentKey,
        int amount,
        String orderId
) {
    public static ConfirmPaymentRequestDTO from(ConfirmPaymentRequest req) {
        return new ConfirmPaymentRequestDTO(
                req.getTradeId(),
                req.getPaymentKey(),
                req.getAmount(),
                req.getOrderId()
        );
    }
}
