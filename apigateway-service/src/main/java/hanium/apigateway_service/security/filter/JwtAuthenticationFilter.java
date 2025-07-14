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
            "/user/auth/login", "/user/auth/signup", "/user/health-check"
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

        // 요청에서 Access, Refresh 토큰 추출
        String refreshToken = jwtUtil.extractRefreshToken(request);

        // Refresh 토큰이 존재하는 경우 -> Refresh, Access 재발급
        if (!refreshToken.equals("NULL")) {
            checkRefreshTokenAndReissue(response, refreshToken);
            return;
        }

        // Refresh 없고 Access 존재하는 경우 -> Access 토큰 인증해 Authentication 객체 생성
        String accessToken = jwtUtil.extractAccessToken(request.getHeader("Authorization"));
        SecurityContextHolder.getContext()
                .setAuthentication(jwtUtil.authenticateToken(accessToken));
        filterChain.doFilter(request, response);
    }

    private void checkRefreshTokenAndReissue(HttpServletResponse response, String refreshToken) {
        // 데이터베이스에서 Refresh 토큰 찾음 -> 검증
        // 해당 토큰으로 email 찾음
        // 기존 Refresh 토큰 삭제
        // 새 Access, Refresh 발급
    }
}
