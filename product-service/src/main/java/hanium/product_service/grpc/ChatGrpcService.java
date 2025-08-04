package hanium.product_service.grpc;

import chatroom.Chatroom;
import chatroom.ChatroomServiceGrpc;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.mapper.ChatGrpcMapper;
import hanium.product_service.mapper.ProductGrpcMapper;
import hanium.product_service.service.ChatService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatGrpcService extends ChatroomServiceGrpc.ChatroomServiceImplBase {
    private final ChatService chatService;

    @Override
    public void createChatroom(Chatroom.CreateChatroomRequest request, StreamObserver<Chatroom.CreateChatroomResponse> responseObserver) {
        CreateChatroomResponseDTO dto = chatService.createChatroom(CreateChatroomRequestDTO.from(request));
        responseObserver.onNext(ChatGrpcMapper.toCreateChatroomResponseGrpc(dto));
        responseObserver.onCompleted();
    }
}
