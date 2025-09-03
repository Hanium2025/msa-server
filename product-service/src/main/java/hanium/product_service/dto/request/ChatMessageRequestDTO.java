package hanium.product_service.dto.request;

import hanium.common.proto.product.ChatMessage;
import hanium.product_service.domain.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatMessageRequestDTO {
    private long chatroomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long timestamp;
    private MessageType messageType;
    private List<String> imageUrls;

    //grpc -> dto
    public static ChatMessageRequestDTO from(ChatMessage msg) {
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

    private static MessageType mapType(hanium.common.proto.product.MessageType type) {
        if (type == null) {
            return MessageType.TEXT;
        }
        return switch (type) {
            case IMAGE -> MessageType.IMAGE;
            case DIRECT_REQUEST -> MessageType.DIRECT_REQUEST;
            case DIRECT_ACCEPT -> MessageType.DIRECT_ACCEPT;
            case PARCEL_REQUEST -> MessageType.PARCEL_REQUEST;
            case PARCEL_ACCEPT -> MessageType.PARCEL_ACCEPT;
            case PAYMENT_REQUEST -> MessageType.PAYMENT_REQUEST;
            case PAYMENT_DONE -> MessageType.PAYMENT_DONE;
            case ADDRESS_REGISTER -> MessageType.ADDRESS_REGISTER;
            case ADDRESS_REGISTER_DONE -> MessageType.ADDRESS_REGISTER_DONE;

            default -> MessageType.TEXT;
        };
    }
}
