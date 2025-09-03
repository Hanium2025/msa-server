package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.chat.response.ChatMessageResponseDTO;
import hanium.apigateway_service.mapper.ChatMessageMapperForGateway;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import hanium.common.proto.product.*;
@Service
@RequiredArgsConstructor
public class ChatGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    //채팅방 별 메시지 가져오기
    public List<ChatMessageResponseDTO> getAllMessagesByChatroomId(Long chatroomId) {
        GetAllMessagesByChatroomIdRequest requestChatroomId = ChatMessageMapperForGateway.chatroomIdToGrpc(chatroomId);
        GetAllMessagesByChatroomResponse response = stub.getAllMessagesByChatroomId(requestChatroomId);
        return ChatMessageResponseDTO.from(response);

    }
}
