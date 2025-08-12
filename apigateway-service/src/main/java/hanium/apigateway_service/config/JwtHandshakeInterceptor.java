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
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                log.info("WebSocket JWT header 인증 시도: {}", token);
            } else {
                // WebSocket query string 방식 (wss://...?token=xxx) 지원
                token = httpRequest.getParameter("token");
                log.info("WebSocket JWT parameter 인증 시도: {}", token);
            }

            if (token != null) {
                try {
                    if (jwtUtil.isTokenValid(token)) {
                        Long userId = jwtUtil.extractId(token);
                        log.info("userId: {}", userId);
                        attributes.put("userId", userId);
                        return true;
                    } else {
                        throw new CustomException(ErrorCode.INVALID_TOKEN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
