package hanium.product_service.mapper;

import chatroom.Chatroom;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;

public class ChatGrpcMapper {
    //dto -> grpc
    public static Chatroom.CreateChatroomResponse toCreateChatroomResponseGrpc(CreateChatroomResponseDTO dto) {
        return Chatroom.CreateChatroomResponse.newBuilder()
                .setChatroomId(dto.getChatroomId())
                .build();
    }
}