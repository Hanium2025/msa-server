package hanium.apigateway_service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.user.response.TokenResponseDTO;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.apigateway_service.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserGrpcClient userGrpcClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
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
        String refreshToken = jwtUtil.extractRefreshToken(request.getHeader("Authorization-refresh"));

        // Refresh 토큰이 존재하는 경우 -> Refresh, Access 재발급, 필터 진행 X
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

    private void checkRefreshTokenAndReissue(HttpServletResponse response,
                                             String refreshToken) throws IOException {
        TokenResponseDTO dto = TokenResponseDTO.from(userGrpcClient.reissueToken(refreshToken));
        ResponseDTO<TokenResponseDTO> result = new ResponseDTO<>(
                dto, HttpStatus.OK, "요청에 Refresh 토큰이 확인되어, 토큰 재발급에 성공했습니다."
        );
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}