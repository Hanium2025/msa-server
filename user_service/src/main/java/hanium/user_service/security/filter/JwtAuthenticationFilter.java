package hanium.user_service.security.filter;

import hanium.user_service.exception.CustomException;
import hanium.user_service.security.common.JwtUtil;
import hanium.user_service.security.service.JwtAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationService jwtAuthenticationService;

    private final List<String> NO_CHECK_URLS = new ArrayList<>(Arrays.asList(
            "/api/auth/login", "/api/auth/signup"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("🔗 들어온 URI: {}", request.getRequestURI());

        // 로그인, 회원가입은 Authorization 헤더 검사 pass
        if (NO_CHECK_URLS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            log.info("✅ 필터 pass됨");
            return;
        }

        String authHeader = request.getHeader("Authorization");
        try {
            // "Authorization" 헤더에서 JWT token 추출
            jwtUtil.extractFromHeader(authHeader)
                    .ifPresent(token -> {
                        Authentication authentication = jwtAuthenticationService.authenticateToken(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            log.error("⚠️ 에러 발생: {}", e.getMessage());
            response.setStatus(e.getErrorCode().getHttpStatus().value());
            response.getWriter().write(e.getErrorCode().getMessage());
        }
    }
}
