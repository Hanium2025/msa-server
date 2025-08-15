package hanium.product_service.grpc;

import chat.Chat;
import chat.ChatServiceGrpc;
import hanium.product_service.dto.request.ChatMessageRequestDTO;
import hanium.product_service.s3.PresignService;
import hanium.product_service.service.ChatService;
import io.grpc.Status;
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
    private final PresignService presign;
    @Override
    public StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatResponseMessage> responseObserver) {
        return chatService.chat(responseObserver); // 서비스에 위임

    }
    @Override
    public void createPresignedUrls(Chat.CreatePresignedUrlsRequest req,
                                    StreamObserver<Chat.CreatePresignedUrlsResponse> resp){
        try{
            var urls = presign.issue(req.getChatroomId(),req.getCount(), req.getContentType());
            var b = Chat.CreatePresignedUrlsResponse.newBuilder();

            for(var u : urls){
                b.addUrls(Chat.PresignedUrl.newBuilder()
                        .setPutUrl(u.putUrl())
                        .setGetUrl(u.getUrl())
                        .setKey(u.key())
                        .build());
            }
            resp.onNext(b.build());
            resp.onCompleted();
        }catch (Exception e){
            resp.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
