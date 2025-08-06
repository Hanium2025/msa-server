//package hanium.apigateway_service.config;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import hanium.apigateway_service.security.JwtUtil;
//import hanium.common.exception.CustomException;
//import hanium.common.exception.ErrorCode;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwt;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//@RequiredArgsConstructor
//public class JwtHandshakeInterceptor implements HandshakeInterceptor {
//    @Value("${jwt.secret}")
//    private String secret;
//    private JwtUtil jwtUtil;
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
//            HttpServletRequest httpRequest = servletRequest.getServletRequest();
//            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String accessToken = authHeader.substring(7);
//                try {
//                    if (jwtUtil.isTokenValid(accessToken)) {
//
//                        Long userId = JWT.require(Algorithm.HMAC512(secret)).build()
//                                .verify(accessToken)
//                                .getClaim("email")
//                                .asString();
//
////                        Long userId = Long.parseLong(userstrId); // subject에 userId가 들어있다고 가정
////                        attributes.put("userId", userId); // 세션에 저장
//                        return true;
//                    } else {
//                        throw new CustomException(ErrorCode.INVALID_TOKEN);
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
//
//    }
//}
