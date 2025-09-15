package hanium.apigateway_service.dto.product.request;

public record SavePayInfoRequestDTO(
        String orderId,
        Long totalPrice
) {
}
