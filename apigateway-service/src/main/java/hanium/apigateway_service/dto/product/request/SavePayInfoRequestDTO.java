package hanium.apigateway_service.dto.product.request;

public record SavePayInfoRequestDTO(
        String orderId, // 주문번호
        Long amount // 최종 결제 금액
) {
}
