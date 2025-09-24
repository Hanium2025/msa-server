package hanium.product_service.service;

import hanium.product_service.dto.request.ConfirmPaymentRequestDTO;

public interface TossPaymentService {

    void confirmPayment(ConfirmPaymentRequestDTO dto);
}
