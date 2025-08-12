package hanium.product_service.grpc;

import chat.Chat;
import chat.ChatServiceGrpc;
import hanium.product_service.dto.request.ChatMessageRequestDTO;
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
public class ChattingGrpcService extends ChatServiceGrpc.ChatServiceImplBase {
    private final ChatService chatService;
    @Override
    public StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatResponseMessage> responseObserver) {
        return chatService.chat(responseObserver); // 서비스에 위임

    }
}
