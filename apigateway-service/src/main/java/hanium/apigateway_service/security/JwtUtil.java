package hanium.apigateway_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.user.GetAuthorityResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Component
@Transactional
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtUtil {

    // application.yml에 설정된 값 가져오기
    @Value("${jwt.secret}")
    private String secret;
    private final UserGrpcClient userGrpcClient;

    // 사용할 상수 정의
    private static final String EMAIL_CLAIM = "email"; // username 클레임
    private static final String BEARER = "Bearer ";


    /**
     * http 요청 헤더에서 Access 토큰을 추출합니다.
     *
     * @param authorizationHeader 요청 헤더 키 값 ("Authorization")
     * @return Access 토큰
     */
    public String extractAccessToken(String authorizationHeader) throws CustomException {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new CustomException(ErrorCode.NULL_ACCESS_TOKEN);
        } else if (!authorizationHeader.startsWith(BEARER)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_BEARER);
        } else {
            return authorizationHeader.substring(BEARER.length());
        }
    }

    /**
     * http 요청 쿠키에서 Refresh 토큰을 추출합니다.
     *
     * @param request http 요청
     * @return Refresh 토큰 또는 "NULL" 문자열
     */
    public static String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return "NULL";
        }
        Cookie cookie = Arrays.stream(request.getCookies()).filter(c -> c
                        .getName().equals("RefreshToken")).findFirst()
                .orElse(null);
        return Objects.requireNonNull(cookie).getValue();
    }

    /**
     * Access 토큰의 Claim 중 email 키에서 사용자 이메일을 추출합니다.
     *
     * @param accessToken 전달된 Access 토큰
     * @return 사용자 이메일
     */
    public String extractEmail(String accessToken) throws CustomException {
        if (isTokenValid(accessToken)) {
            return JWT.require(Algorithm.HMAC512(secret)).build()
                    .verify(accessToken)
                    // 검증됐다면 email 가져옴
                    .getClaim(EMAIL_CLAIM)
                    // 값을 String로 변환
                    .asString();
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰의 유효성을 검증해 여부를 반환합니다.
     *
     * @param token 전달된 토큰
     * @return 유효한 토큰인가? (true/false)
     */
    public boolean isTokenValid(String token) {
        try {
            /*
             * require: HMAC512 알고리즘과 secret을 사용해 토큰 서명을 검증하도록 설정
             * build:   JWT 검증기 생성
             * verify:  파라미터의 token 검증
             */
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("⚠️ JwtUtil-isTokenValid: 유효하지 않은 토큰: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 사용자 요청의 Access 토큰을 검증 후, 유효하다면 사용자 권한 정보(authority)를 가져옵니다.
     * 가져온 권한을 통해 UsernamePasswordAuthenticationToken 객체를 생성해 반환합니다.
     *
     * @param accessToken 전달된 Access 토큰
     * @return 인증 완료된 Authentication 객체
     */
    public Authentication authenticateToken(String accessToken) {
        // 토큰 유효성 검사
        if (!isTokenValid(accessToken)) {
            log.error("⚠️ authenticateToken: Access 토큰이 유효하지 않습니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 사용자 이메일 추출
        String email = extractEmail(accessToken);
        log.info("✅ 요청 사용자 이메일: {}", email);

        // 이메일로 사용자 권한 정보 가져오기
        GetAuthorityResponse protoResponse = userGrpcClient.getAuthority(email);
        String authority = protoResponse.getAuthority();

        // UsernamePasswordAuthenticationToken에 들어갈
        // Collection<? extends GrantedAuthority> authorities 생성
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authority));

        return new UsernamePasswordAuthenticationToken(
                protoResponse.getMemberId(), accessToken, authorities
        );
    }

    /**
     * Refresh 토큰 문자열로 쿠키를 생성해 반환합니다.
     *
     * @param refreshToken 전달할 Refresh 토큰
     * @return 헤더의 쿠키 객체
     */
    public static Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setPath("/user/auth/refresh");
        cookie.setMaxAge(24 * 60 * 60); // 24h
        cookie.setHttpOnly(true);   // JS로 접근 불가, 탈취 위험 감소
        return cookie;
    }

    /**
     * 기존 RefreshToken 쿠키를 null, 만료된 상태로 반환하여 삭제합니다.
     *
     * @return 삭제될 쿠키
     */
    public static Cookie removeCookie() {
        Cookie nullCookie = new Cookie("RefreshToken", null);
        nullCookie.setPath("/user/auth/refresh");
        nullCookie.setMaxAge(0);
        nullCookie.setHttpOnly(true);
        return nullCookie;
    }
}
