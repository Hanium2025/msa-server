package hanium.apigateway_service.grpc;

import chat.Chat;
import chat.ChatServiceGrpc;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;
import hanium.apigateway_service.mapper.ChatMessageMapperForGateway;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession; // ✅ Spring MVC용
import org.springframework.web.socket.TextMessage;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcChatStreamClient {

    @GrpcClient("product-service")
    private ChatServiceGrpc.ChatServiceStub stub;
    private StreamObserver<Chat.ChatMessage> requestObserver;

    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // 사용자 세션 등록
    public void registerSession(String userId, WebSocketSession session){
        sessionMap.put(userId, session);
        if(requestObserver == null) startStream();
    }
    // 사용자 세션 제거
    public void removeSession(String userId) {
        sessionMap.remove(userId);
    }

    // 클라이언트로부터 받은 메시지를 gRPC로 전송
    public void sendMessage(ChatMessageRequestDTO dto) {
        Chat.ChatMessage grpcMessage = ChatMessageMapperForGateway.toGrpc(dto);
        requestObserver.onNext(grpcMessage);
    }

    private void startStream() {
        requestObserver = stub.chat(new StreamObserver<Chat.ChatMessage>() {
            @Override
            public void onNext(Chat.ChatMessage msg) {
                String receiverId = String.valueOf(msg.getReceiverId());
                WebSocketSession session = sessionMap.get(receiverId);

                if(session != null && session.isOpen()){
                    try{
                        session.sendMessage(new TextMessage(msg.getContent()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("gRPC error: " + t.getMessage());

            }

            @Override
            public void onCompleted() {
                log.error("gRPC stream completed");
            }
        });
    }
}
