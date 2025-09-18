package hanium.apigateway_service.grpc;


import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;
import hanium.apigateway_service.dto.chat.response.ChatMessageResponseDTO;
import hanium.apigateway_service.mapper.ChatMessageMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.product.ProductServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import hanium.common.proto.product.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcChatStreamClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceStub stub;
    private StreamObserver<ChatMessage> requestObserver;

    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 사용자 세션 등록
    public void registerSession(String userId, WebSocketSession session) {
        sessionMap.put(userId, session);
        log.info("Session등록 userID : {} ", userId);
        if (requestObserver == null) ensureStream();
    }

    // 사용자 세션 제거
    public void removeSession(String userId) {
        sessionMap.remove(userId);
    }

    // 클라이언트로부터 받은 메시지를 gRPC로 전송
    public void sendMessage(ChatMessageRequestDTO dto) {

        ensureStream();
        log.info("gRPC Stream 전송 시도: {}: ", dto);
        ChatMessage grpcMessage = ChatMessageMapperForGateway.toGrpc(dto);
        requestObserver.onNext(grpcMessage);
        log.info("✅ gRPC onNext 호출 완료");

    }
    private void ensureStream(){
        if(requestObserver != null)
            return;
        synchronized (this){
            if(requestObserver == null){
                startStream();
            }
            if(requestObserver == null){
                throw new CustomException(ErrorCode.CHAT_STREAM_NOT_AVAILABLE);
            }
        }
    }

    private void startStream() {
        requestObserver = stub.chat(new StreamObserver<>() {
            @Override
            public void onNext(ChatResponseMessage msg) {

                log.info("↩️ gRPC onNext: type={}, room={}, sender={}, receiver={}",
                        msg.getType(), msg.getChatroomId(), msg.getSenderId(), msg.getReceiverId());

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

            //public void sendDirectRequest(Long chatroomId, Long requesterId){}


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


    public void sendDirectRequest(Long chatroomId, Long requestId, Long receiverId ){
        ensureStream();

        ChatMessage grpcMessage = ChatMessage.newBuilder()
                .setChatroomId(chatroomId)
                .setSenderId(requestId)
                .setReceiverId(receiverId)
                .setType(MessageType.DIRECT_REQUEST)
                .setContent("직거래 요청이 들어왔습니다.")
                .build();

        requestObserver.onNext(grpcMessage);
        log.info("직거래 요청 onNext 완료: room={},from={},to={}", chatroomId,requestId,receiverId);


    }

    public void sendDirectAccept(Long chatroomId, Long requestId, Long receiverId ){
        ensureStream();

        ChatMessage grpcMessage = ChatMessage.newBuilder()
                .setChatroomId(chatroomId)
                .setSenderId(requestId)
                .setReceiverId(receiverId)
                .setType(MessageType.DIRECT_ACCEPT)
                .setContent("직거래 요청이 수락됐어요. 거래 평가를 위해\n 거래가 끝나면 + 버튼을 눌러 '거래완료'를 눌러주세요.")
                .build();

        requestObserver.onNext(grpcMessage);
        log.info("직거래 수락 onNext 완료: room={},from={},to={}", chatroomId,requestId,receiverId);


    }

    public void sendCompleteTrade(Long chatroomId, Long requestId, Long receiverId ){
        ensureStream();
        ChatMessage grpcMessage = ChatMessage.newBuilder()
                .setChatroomId(chatroomId)
                .setSenderId(requestId)
                .setReceiverId(receiverId)
                .setType(MessageType.TRADE_COMPLETE)
                .setContent("거래가 완료되었습니다.")
                .build();
        requestObserver.onNext(grpcMessage);
        log.info("거래 완료 onNext 완료: room={},from={},to={}", chatroomId,requestId,receiverId);


    }

    public void sendParcelRequest(Long chatroomId, Long requestId, Long receiverId ){
        ensureStream();

        ChatMessage grpcMessage = ChatMessage.newBuilder()
                .setChatroomId(chatroomId)
                .setSenderId(requestId)
                .setReceiverId(receiverId)
                .setType(MessageType.PARCEL_REQUEST)
                .setContent("택배거래 요청이 들어왔습니다.")
                .build();

        requestObserver.onNext(grpcMessage);
        log.info("택배 거래 요청 onNext 완료: room={},from={},to={}", chatroomId,requestId,receiverId);


    }
    public void sendParcelAccept(Long chatroomId, Long requestId, Long receiverId ){
        ensureStream();

        ChatMessage grpcMessage = ChatMessage.newBuilder()
                .setChatroomId(chatroomId)
                .setSenderId(requestId)
                .setReceiverId(receiverId)
                .setType(MessageType.PARCEL_ACCEPT)
                .setContent("택배 거래 요청이 수락되었습니다. \n 결제가 요청되었어요.")
                .build();

        requestObserver.onNext(grpcMessage);
        log.info("택배 거래 수락 onNext 완료: room={},from={},to={}", chatroomId,requestId,receiverId);


    }

}
