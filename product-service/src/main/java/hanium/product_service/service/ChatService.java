package hanium.product_service.service;

import chat.Chat;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.ChatMessageResponseDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.GetMyChatroomResponseDTO;
import io.grpc.stub.StreamObserver;
import java.util.List;

public interface ChatService{

    CreateChatroomResponseDTO createChatroom(CreateChatroomRequestDTO requestDTO);

    // 👇 BiDi Streaming용 메서드 추가
    StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatResponseMessage> responseObserver);

    //채팅방 조회
     List<GetMyChatroomResponseDTO> getMyChatrooms(Long memberId);


     //채팅방별 메시지 조회
    List<ChatMessageResponseDTO> getAllMessageByChatroomId(Long chatroomId);
}
