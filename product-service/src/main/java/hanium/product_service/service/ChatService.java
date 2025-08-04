package hanium.product_service.service;

import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;

public interface ChatService {

    CreateChatroomResponseDTO createChatroom(CreateChatroomRequestDTO requestDTO);
}
