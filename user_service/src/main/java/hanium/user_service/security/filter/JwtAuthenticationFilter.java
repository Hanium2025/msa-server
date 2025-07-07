package hanium.user_service.security.filter;

import hanium.user_service.exception.CustomException;
import hanium.user_service.security.common.JwtUtil;
import hanium.user_service.security.service.JwtAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
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
            response.setStatus(e.getErrorCode().getHttpStatus().value());
            response.getWriter().write(e.getErrorCode().getMessage());
        }
    }
}
