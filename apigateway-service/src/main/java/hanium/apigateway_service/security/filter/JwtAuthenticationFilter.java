package hanium.apigateway_service.security.filter;

import hanium.apigateway_service.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 매 요청 전, 사용자의 JWT 토큰을 검사하는 필터입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> NO_CHECK_URLS = new ArrayList<>(Arrays.asList(
            "/user/auth",
            "/user/sms",
            "/health",
            "/health-check",
            "/actuator"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        //  WebSocket 경로 예외 처리
        if (uri.startsWith("/wss")) {
            log.info("✅ WebSocket 경로 필터 패스됨: {}", uri);
            filterChain.doFilter(request, response);
            log.info("✅ 필터 pass됨");
            return;
        }
        // 필터 검사 pass
        for (String prefix : NO_CHECK_URLS) {
            if (request.getRequestURI().startsWith(prefix)) {
                log.info("✅ JWT 검사 필터 pass됨: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Access 존재하는 경우 -> Access 토큰 인증해 Authentication 객체 생성
        String accessToken = jwtUtil.extractAccessToken(request.getHeader("Authorization"));
        SecurityContextHolder.getContext()
                .setAuthentication(jwtUtil.authenticateToken(accessToken));

        filterChain.doFilter(request, response);
    }
}