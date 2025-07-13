package hanium.apigateway_service.security.filter;

import hanium.apigateway_service.security.JwtUtil;
import hanium.apigateway_service.security.service.JwtAuthenticationService;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final List<String> NO_CHECK_URLS = new ArrayList<>(Arrays.asList(
            "/user/auth/login", "/user/auth/signup"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 로그인, 회원가입은 Authorization 헤더 검사 pass
        if (NO_CHECK_URLS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            log.info("✅ 필터 pass됨");
            return;
        }

        String header = request.getHeader("Authorization");
        try {
            // "Authorization" 헤더에서 JWT token 추출
            jwtUtil.extractFromHeader(header)
                    .ifPresent(token -> {
                        log.info("✅ on JwtAuthenticationFilter - 추출된 토큰: {}", token);
                        Authentication authentication = null;
                        try {
                            authentication = jwtAuthenticationService.authenticateToken(token);
                        } catch (Exception e) {
                            throw new CustomException(ErrorCode.TOKEN_AUTH_ERROR);
                        }
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            log.error("⚠️ 에러 발생: {}", e.getMessage());
            throw e;
        }
    }
}
