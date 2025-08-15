package hanium.apigateway_service.grpc;

import chat.Chat;
import chat.ChatServiceGrpc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;
import hanium.apigateway_service.dto.chat.response.ChatMessageResponseDTO;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();
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

        if(requestObserver == null){
            startStream();
            if(requestObserver == null){
                log.warn("gRPC stream not ready");
                return;
            }
        }
        log.info("gRPC Stream 전송 시도: {}: ", dto);
        Chat.ChatMessage grpcMessage = ChatMessageMapperForGateway.toGrpc(dto);
        requestObserver.onNext(grpcMessage);
        log.info("✅ gRPC onNext 호출 완료");

    }

    private void startStream() {
        requestObserver = stub.chat(new StreamObserver<Chat.ChatResponseMessage>() {
            @Override
            public void onNext(Chat.ChatResponseMessage msg) {
                // 1) 공통 DTO 생성
                ChatMessageResponseDTO base = ChatMessageResponseDTO.builder()
                        .messageId(msg.getMessageId())
                        .chatroomId(msg.getChatroomId())
                        .senderId(msg.getSenderId())
                        .receiverId(msg.getReceiverId())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .type(msg.getType().name())
                        .imageUrl(msg.getImageUrlsList())
                        .build();

                // 2) 발신자(mine = true), 수신자(mine = false) 각각 전송
                sendToWs(String.valueOf(msg.getSenderId()), base.toBuilder().mine(true).build());
                sendToWs(String.valueOf(msg.getReceiverId()), base.toBuilder().mine(false).build());

            }
            // DTO를 JSON으로 바꿔서 WebSocket으로 보내는 헬퍼
            private void sendToWs(String userId, ChatMessageResponseDTO dto) {
                WebSocketSession session = sessionMap.get(userId);
                if (session == null || !session.isOpen()) {
                    log.debug("웹소켓 세션 없음/닫힘: {}", userId);
                    return;
                }
                try {
                    String json = objectMapper.writeValueAsString(dto); // ✅ DTO를 JSON으로
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    log.warn("메시지 전송 실패 → 세션 제거: {}", userId, e);
                    sessionMap.remove(userId);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("gRPC 오류 발생", t);
                requestObserver = null;
                startStream(); // 재연결 시도

            }

            @Override
            public void onCompleted() {
                log.error("gRPC stream completed");
                requestObserver = null;
                startStream(); // 재연결 시도
            }
        });
    }
}
