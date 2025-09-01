package hanium.apigateway_service.grpc;

import chat.Chat;
import chat.ChatServiceGrpc;
import hanium.apigateway_service.dto.chat.response.ChatMessageResponseDTO;
import hanium.apigateway_service.mapper.ChatMessageMapperForGateway;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatGrpcClient {

    @GrpcClient("product-service")
    private ChatServiceGrpc.ChatServiceBlockingStub chatServiceBlockingStub;

    //채팅방 별 메시지 가져오기
    public List<ChatMessageResponseDTO> getAllMessagesByChatroomId(Long chatroomId) {
        Chat.GetAllMessagesByChatroomIdRequest requestChatroomId = ChatMessageMapperForGateway.chatroomIdToGrpc(chatroomId);
        Chat.GetAllMessagesByChatroomResponse response = chatServiceBlockingStub.getAllMessagesByChatroomId(requestChatroomId);
        return ChatMessageResponseDTO.from(response);

    }
}
