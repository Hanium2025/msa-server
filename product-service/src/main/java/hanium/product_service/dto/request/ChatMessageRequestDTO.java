package hanium.product_service.dto.request;


import chat.Chat;
import hanium.product_service.domain.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatMessageRequestDTO {
    private Long chatroomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long timestamp;
    private MessageType messageType;
    private List<String> imageUrls;

    //grpc -> dto
    public static ChatMessageRequestDTO from(Chat.ChatMessage msg){
        return ChatMessageRequestDTO.builder()
                    .chatroomId(msg.getChatroomId())
                .receiverId(msg.getReceiverId())
                .senderId(msg.getSenderId())
                .content(msg.getContent())
                .timestamp(msg.getTimestamp())
                .messageType(mapType(msg.getType()))
                .imageUrls(msg.getImageUrlsList())
                .build();

    }

    private static MessageType mapType(Chat.MessageType type) {
        if(type == null){
            return MessageType.TEXT;
        }
        switch (type){
            case IMAGE : return MessageType.IMAGE;
            case NOTICE: return MessageType.NOTICE;
            case TEXT:
            default: return MessageType.TEXT;
        }
    }

}
