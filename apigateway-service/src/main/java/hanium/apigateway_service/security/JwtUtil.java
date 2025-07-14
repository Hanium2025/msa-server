package hanium.apigateway_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.grpc.Metadata;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtUtil {

    // application.yml에 설정된 값 가져오기
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    // 사용할 상수 정의
    private static final String ACCESS_TOKEN = "AccessToken"; // 토큰 제목 (sub)
    private static final String REFRESH_TOKEN = "RefreshToken";
    private static final String EMAIL_CLAIM = "email"; // username 클레임
    private static final String BEARER = "Bearer ";
    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    // JWT 토큰 생성
    public String createAccessToken(String email) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN) // 토큰 제목을 "AccessToken"으로 지정
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .withClaim(EMAIL_CLAIM, email) // 클레임 키 "email"에 받아온 email 값 추가
                .sign(Algorithm.HMAC512(secret)); // 지정한 secret 값으로 암호화
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .sign(Algorithm.HMAC512(secret));
    }

    // 토큰에서 사용자 이메일 추출
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(
                    JWT.require(Algorithm.HMAC512(secret)).build()
                            .verify(accessToken)
                            // 검증됐다면 USERNAME_CLAIM, 즉 email 가져옴
                            .getClaim(EMAIL_CLAIM)
                            // 값을 String로 변환
                            .asString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    // HTTP 요청 헤더에서 Access 토큰 추출
    public Optional<String> extractAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            return Optional.of(authorizationHeader.substring(BEARER.length()));
        }
        return Optional.empty();
    }

    // HTTP 요청 쿠키에서 Refresh 토큰 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie cookie = Arrays.stream(request.getCookies()).filter(c -> c
                        .getName().equals("RefreshToken")).findFirst()
                .orElse(null);
        if (cookie != null) {
            return Optional.ofNullable(cookie.getValue());
        } else {
            return Optional.empty();
        }
    }

    // 토큰에서 만료시간 추출
    public Optional<LocalDateTime> extractExpiration(String token) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret)).build().verify(token)
                    .getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // gRPC 메타데이터에서 토큰 추출
    public Optional<String> extractFromMetadata(Metadata metadata) {
        String authorizationHeader = metadata.get(AUTHORIZATION_METADATA_KEY);
        return extractAccessToken(authorizationHeader);
    }

    // 토큰 유효성 검증
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
}
