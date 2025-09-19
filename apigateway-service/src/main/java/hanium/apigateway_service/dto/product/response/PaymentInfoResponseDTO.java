package hanium.apigateway_service.dto.product.response;

public record PaymentInfoResponseDTO(
        Long paymentId,
        String clientOrderId,
        String clientPaymentKey,
        Long tradeId,
        Long sellerId,
        Long buyerId,
        String paymentStatus
) {
}
