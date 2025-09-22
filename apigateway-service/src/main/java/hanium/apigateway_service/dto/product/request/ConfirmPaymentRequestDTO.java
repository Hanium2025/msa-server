package hanium.apigateway_service.dto.product.request;

public record ConfirmPaymentRequestDTO(
        Long tradeId,
        String paymentKey,
        int amount,
        String orderId
) {
}
