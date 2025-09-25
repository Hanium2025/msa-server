package hanium.apigateway_service.config;

import hanium.apigateway_service.security.JwtUtil;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String token = null;

        // 1) Authorization 헤더 시도 (브라우저는 안 붙음)
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("WS JWT header 인증 시도: {}", token);
        }

        // 2) 쿼리스트링 시도 (브라우저 WebSocket 기본 방식)
        if (token == null) {
            String query = request.getURI().getQuery(); // e.g. token=xxx&roomId=1
            if (query != null) {
                for (String kvp : query.split("&")) {
                    String[] kv = kvp.split("=", 2);
                    if (kv.length == 2 && "token".equals(kv[0])) {
                        token = kv[1];
                        break;
                    }
                }
            }
            log.info("WS JWT query 인증 시도: {}", token);
        }

        if (token != null) {
            try {
                if (jwtUtil.isTokenValid(token)) {
                    Long userId = jwtUtil.extractId(token);
                    log.info("WS 인증 성공 userId={}", userId);
                    attributes.put("userId", userId);
                    return true;
                } else {
                    log.warn(" WS 토큰 검증 실패");
                    return false;
                }
            } catch (Exception e) {
                log.error("WS JWT 처리 중 오류", e);
                return false;
            }
        }

        log.warn(" WS 토큰이 없음");
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
