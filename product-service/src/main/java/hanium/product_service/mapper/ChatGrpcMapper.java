package hanium.product_service.mapper;

import hanium.common.proto.product.CreateChatroomResponse;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;

public class ChatGrpcMapper {
    //dto -> grpc
    public static CreateChatroomResponse toCreateChatroomResponseGrpc(CreateChatroomResponseDTO dto) {
        return CreateChatroomResponse.newBuilder()
                .setChatroomId(dto.getChatroomId())
                .build();
    }
}