package hanium.apigateway_service.mapper;

import chatroom.Chatroom;
import hanium.apigateway_service.dto.chat.request.CreateChatroomRequestDTO;


public class ChatGrpcMapperForGateway {
    public static Chatroom.CreateChatroomRequest toGrpc(CreateChatroomRequestDTO dto) {
        return Chatroom.CreateChatroomRequest.newBuilder()
                .setProductId(dto.getProductId())
                .setSenderId(dto.getSenderId())
                .setReceiverId(dto.getReceiverId())
                .build();
    }
}
