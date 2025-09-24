package hanium.product_service.service;

import hanium.common.proto.product.*;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.ChatMessageResponseDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.GetMyChatroomResponseDTO;
import hanium.product_service.dto.response.TradeInfoDTO;
import io.grpc.stub.StreamObserver;
import java.util.List;

public interface ChatService{

    CreateChatroomResponseDTO createChatroom(CreateChatroomRequestDTO requestDTO);

    // 👇 BiDi Streaming용 메서드 추가
    StreamObserver<ChatMessage> chat(StreamObserver<ChatResponseMessage> responseObserver);

    //채팅방 조회
     List<GetMyChatroomResponseDTO> getMyChatrooms(Long memberId);


     //채팅방별 메시지 조회
    List<ChatMessageResponseDTO> getAllMessageByChatroomId(Long chatroomId);

    //채팅방별 상품 아이디와 판매자 아이디 조회
    TradeInfoDTO getTradeInfoByChatroomIdAndMemberId(Long chatroomId, Long memberId);


}
