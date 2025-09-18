package hanium.product_service.service;

import hanium.product_service.dto.request.CreateWayBillRequestDTO;

public interface DeliveryService {
    void createWayBill(CreateWayBillRequestDTO dto);
}
