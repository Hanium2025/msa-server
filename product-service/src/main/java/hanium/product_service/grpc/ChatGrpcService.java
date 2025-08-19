package hanium.product_service.grpc;

import chatroom.Chatroom;
import chatroom.ChatroomServiceGrpc;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.GetMyChatroomResponseDTO;
import hanium.product_service.mapper.ChatGrpcMapper;
import hanium.product_service.service.ChatService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

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

    @Override
    public void getMyChatrooms(Chatroom.ListMyChatroomsRequest request,
                               StreamObserver<Chatroom.ListMyChatroomsResponse> responseObserver) {

        Long memberId = request.getMemberId();
        List<GetMyChatroomResponseDTO> items = chatService.getMyChatrooms(memberId);

        Chatroom.ListMyChatroomsResponse.Builder resp = Chatroom.ListMyChatroomsResponse.newBuilder();

        for (GetMyChatroomResponseDTO dto : items) {
            long latestMillis = dto.getLatestTime() == null
                    ? 0L
                    : dto.getLatestTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            Chatroom.ChatroomSummary summary =
                    Chatroom.ChatroomSummary.newBuilder()
                            .setChatroomId(dto.getChatroomId())
                            .setRoomName(dto.getRoomName() == null ? "" : dto.getRoomName())
                            .setLatestMessage(dto.getLatestMessage() == null ? "" : dto.getLatestMessage())
                            .setProductId(dto.getProductId() == null ? 0L : dto.getProductId())
                            .setOpponentId(dto.getOpponentId() == null ? 0L : dto.getOpponentId())
                            .setLatestTime(latestMillis)
                            .build();

            resp.addItems(summary);
        }

        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();
    }
}
