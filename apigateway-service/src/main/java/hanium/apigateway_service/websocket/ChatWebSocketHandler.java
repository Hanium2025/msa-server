package hanium.apigateway_service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.chat.request.ChatMessageRequestDTO;
import hanium.apigateway_service.grpc.GrpcChatStreamClient;
import hanium.apigateway_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 연결을 처리하는 핸들러입니다.
 * 클라이언트가 WebSocket으로 접속하면 연결을 관리하고,
 * 들어온 메시지를 gRPC를 통해 product-service로 중계합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {
    private final GrpcChatStreamClient grpcChatStreamClient;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    // WebSocket 세션 저장소: userId -> WebSocketSession
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 클라이언트가 WebSocket 연결을 성공했을 때 호출됩니다.
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        try {
            Object userIdObj = session.getAttributes().get("userId");

            if (userIdObj == null) {
                log.warn("❌ WebSocket 연결 시 id가 null입니다.");
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            String userId = userIdObj.toString();
            sessions.put(userId, session);
            grpcChatStreamClient.registerSession(userId, session);// gRPC 스트리밍에 세션 등록
        } catch (Exception e) {
            session.close(CloseStatus.BAD_DATA);
            log.warn("WebSocket 연결 실패: {}", e.getMessage());
        }

    }

    /**
     * 클라이언트로부터 메시지를 수신했을 때 호출됩니다.
     */
    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        // 메시지를 JSON → DTO로 변환
        ChatMessageRequestDTO dto = objectMapper.readValue(message.getPayload().toString(), ChatMessageRequestDTO.class);
        log.info("📤 WebSocket 수신: {} ", dto);
        //gRPC를 통해 product-service로 전송
        grpcChatStreamClient.sendMessage(dto);
    }

    /**
     * 메시지 처리 중 오류가 발생했을 때 호출됩니다.
     */
    @Override
    public void handleTransportError(@NotNull WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace(); // 개발 중에는 콘솔 출력, 운영에서는 로깅 처리 권장
    }

    /**
     * 클라이언트와의 연결이 종료되었을 때 호출됩니다.
     */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) throws Exception {
        String userId = session.getAttributes().get("userId").toString();
        sessions.remove(userId); // 세션 제거
        grpcChatStreamClient.removeSession(userId); // gRPC 스트림에서도 세션 제거
    }

    /**
     * 부분 메시지를 지원하지 않음.
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
