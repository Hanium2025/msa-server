package hanium.apigateway_service.mapper;

import chat.Chat;
import chatroom.Chatroom;
import hanium.apigateway_service.dto.chat.request.CreateChatroomRequestDTO;


public class ChatGrpcMapperForGateway {

    //채팅방 생성 요청 dto에서 grpc로 변환
    public static Chatroom.CreateChatroomRequest toGrpc(CreateChatroomRequestDTO dto,Long memberId) {
        return Chatroom.CreateChatroomRequest.newBuilder()
                .setProductId(dto.getProductId())
                .setSenderId(memberId)
                .setReceiverId(dto.getReceiverId())
                .build();
    }

    //내가 참여한 채팅방 조회 요청 dto 에서 grpc로 변환
    public static Chatroom.ListMyChatroomsRequest toGrpc(Long memberId){
        return Chatroom.ListMyChatroomsRequest.newBuilder()
                .setMemberId(memberId).build();
    }


}
