package hanium.apigateway_service.grpc;

import chatroom.Chatroom;
import chatroom.ChatroomServiceGrpc;
import hanium.apigateway_service.dto.chat.request.CreateChatroomRequestDTO;
import hanium.apigateway_service.dto.chat.response.GetMyChatroomResponseDTO;
import hanium.apigateway_service.mapper.ChatGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatroomGrpcClient {

    @GrpcClient("product-service")
    //grpc stub
    private ChatroomServiceGrpc.ChatroomServiceBlockingStub chatroomStub;

    public Long createChatroom(CreateChatroomRequestDTO dto,Long memberId) {
        try {
            Chatroom.CreateChatroomRequest grpcRequest = ChatGrpcMapperForGateway.toGrpc(dto, memberId);

            Chatroom.CreateChatroomResponse response = chatroomStub.createChatroom(grpcRequest);
            return response.getChatroomId();
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    public List<GetMyChatroomResponseDTO> getMyChatrooms(Long memberId){
        try{
            Chatroom.ListMyChatroomsRequest grpcRequest = ChatGrpcMapperForGateway.toGrpc(memberId);
            Chatroom.ListMyChatroomsResponse response =   chatroomStub.getMyChatrooms(grpcRequest);
            return GetMyChatroomResponseDTO.from(response);

        }catch (StatusRuntimeException e){
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }

    }

}
