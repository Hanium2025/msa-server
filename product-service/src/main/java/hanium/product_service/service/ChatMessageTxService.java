package hanium.product_service.service;

import hanium.product_service.domain.Message;
import hanium.product_service.dto.request.ChatMessageRequestDTO;

public interface ChatMessageTxService {
    public Message handleMessage(ChatMessageRequestDTO dto);

}
