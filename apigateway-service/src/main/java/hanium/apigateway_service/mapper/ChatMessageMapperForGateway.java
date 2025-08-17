package hanium.apigateway_service.mapper;
import chat.Chat;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;

public class ChatMessageMapperForGateway {
//dto->grpc
    public static Chat.ChatMessage toGrpc(ChatMessageRequestDTO dto) {
        Chat.ChatMessage.Builder b = Chat.ChatMessage.newBuilder()
                .setChatroomId(dto.getChatroomId())
                .setSenderId(dto.getSenderId())
                .setReceiverId(dto.getReceiverId())
                .setContent(dto.getContent())
                .setTimestamp(System.currentTimeMillis())
                .setType(Chat.MessageType.valueOf(dto.getType().toUpperCase())); // "TEXT"/"IMAGE"/"NOTICE"

        if(dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()){
            b.addAllImageUrls(dto.getImageUrl());
        }
        return b.build();
    }

}
