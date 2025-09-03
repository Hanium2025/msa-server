package hanium.product_service.dto.request;


import hanium.common.proto.product.CreateChatroomRequest;
import lombok.*;

@Getter
@Builder
public class CreateChatroomRequestDTO {
    private Long productId;
    private Long senderId;
    private Long receiverId;

    //grpc -> dto
    public static CreateChatroomRequestDTO from(CreateChatroomRequest request){
        return CreateChatroomRequestDTO.builder()
                .productId(request.getProductId())
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .build();
    }
}
