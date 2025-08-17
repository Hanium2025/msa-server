package hanium.product_service.dto.request;

import chatroom.Chatroom;
import lombok.*;

@Getter
@Builder
public class CreateChatroomRequestDTO {
    private Long productId;
    private Long senderId;
    private Long receiverId;

    //grpc -> dto
    public static CreateChatroomRequestDTO from(Chatroom.CreateChatroomRequest request){
        return CreateChatroomRequestDTO.builder()
                .productId(request.getProductId())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .build();
    }
}
