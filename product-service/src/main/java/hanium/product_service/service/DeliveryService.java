package hanium.product_service.service;

import hanium.product_service.dto.request.CreateWayBillRequestDTO;
import hanium.product_service.dto.response.DeliveryInfoResponseDTO;

public interface DeliveryService {
    void createWayBill(CreateWayBillRequestDTO dto);
    DeliveryInfoResponseDTO getDeliveryInfo(Long tradeId, Long memberId);
}
