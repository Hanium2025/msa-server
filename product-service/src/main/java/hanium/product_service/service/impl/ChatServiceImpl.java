package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Chatroom;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatroomRepository chatroomRepository;
    private final ProductRepository productRepository;
    private final ProfileGrpcClient profileGrpcClient;
    @Override
    public CreateChatroomResponseDTO createChatroom(CreateChatroomRequestDTO requestDTO) {
        Long productId = requestDTO.getProductId();
        Long senderId = requestDTO.getSenderId();
        Long receiverId = requestDTO.getReceiverId();
        // 1. 중복 채팅방 조회
        Optional<Chatroom> existing = chatroomRepository
                .findByProductIdAndSenderIdAndReceiverId(productId, senderId, receiverId);

        if (existing.isPresent()) {
            return new CreateChatroomResponseDTO(existing.get().getId(), "기존 채팅방입니다");
        }
        // 2. 상품명 가져오기
        String productName = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"))
                .getTitle();

        // 3. receiver 닉네임 조회 (gRPC call to user-service)
        String receiverNickname = profileGrpcClient.getNicknameByMemberId(receiverId);

        // 4. 채팅방 이름 생성
        String roomName = receiverNickname + "/" + productName;
        // 5. 채팅방 저장
        Chatroom chatroom = Chatroom.builder()
                .productId(productId)
                .senderId(senderId)
                .receiverId(receiverId)
                .roomName(roomName)
                .build();

        // 중복 채팅방 검사

        Chatroom saved = chatroomRepository.save(chatroom);
        return new CreateChatroomResponseDTO(saved.getId(), "채팅방 생성 성공");
    }
}
