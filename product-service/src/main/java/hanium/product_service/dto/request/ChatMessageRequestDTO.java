package hanium.product_service.dto.request;

import chat.Chat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageRequestDTO {
    private Long chatroomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long timestamp;

    //grpc -> dto
    public static ChatMessageRequestDTO from(Chat.ChatMessage msg){
        return ChatMessageRequestDTO.builder()
                    .chatroomId(msg.getChatroomId())
                .receiverId(msg.getReceiverId())
                .senderId(msg.getSenderId())
                .content(msg.getContent())
                .timestamp(msg.getTimestamp())
                .build();

    }

}
