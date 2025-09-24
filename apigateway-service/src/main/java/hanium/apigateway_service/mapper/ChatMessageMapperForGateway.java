package hanium.apigateway_service.mapper;
import hanium.common.proto.product.*;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;

public class ChatMessageMapperForGateway {
//dto->grpc
    public static ChatMessage toGrpc(ChatMessageRequestDTO dto) {
        ChatMessage.Builder b = ChatMessage.newBuilder()
                .setChatroomId(dto.getChatroomId())
                .setSenderId(dto.getSenderId())
                .setReceiverId(dto.getReceiverId())
                .setContent(dto.getContent())
                .setTimestamp(System.currentTimeMillis())
                .setType(MessageType.valueOf(dto.getType().toUpperCase())); // "TEXT"/"IMAGE"/"NOTICE"

        if(dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()){
            b.addAllImageUrls(dto.getImageUrl());
        }
        return b.build();
    }

    //채팅방별 메시지 조회를 위한 요청메시지 dto에서 grpc로 변환
    public static GetAllMessagesByChatroomIdRequest chatroomIdToGrpc(Long chatroomId){
        return GetAllMessagesByChatroomIdRequest.newBuilder()
                .setChatRoomId(chatroomId).build();
    }


}
