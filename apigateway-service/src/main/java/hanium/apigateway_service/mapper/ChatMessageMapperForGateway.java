package hanium.apigateway_service.mapper;
import chat.Chat;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;

public class ChatMessageMapperForGateway {

    public static Chat.ChatMessage toGrpc(ChatMessageRequestDTO dto) {
        return Chat.ChatMessage.newBuilder()
                .setChatroomId(dto.getChatroomId())
                .setSenderId(dto.getSenderId())
                .setReceiverId(dto.getReceiverId())
                .setContent(dto.getContent())
                .setTimestamp(dto.getTimestamp())
                .build();
    }

}
