package hanium.apigateway_service.grpc;
import hanium.common.proto.product.*;
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
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    public Long createChatroom(CreateChatroomRequestDTO dto,Long memberId) {
        try {
           CreateChatroomRequest grpcRequest = ChatGrpcMapperForGateway.toGrpc(dto, memberId);

           CreateChatroomResponse response = stub.createChatroom(grpcRequest);
            return response.getChatroomId();
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    public List<GetMyChatroomResponseDTO> getMyChatrooms(Long memberId){
        try{
            ListMyChatroomsRequest grpcRequest = ChatGrpcMapperForGateway.toGrpc(memberId);
            ListMyChatroomsResponse response =   stub.getMyChatrooms(grpcRequest);
            return GetMyChatroomResponseDTO.from(response);

        }catch (StatusRuntimeException e){
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }

    }

}
