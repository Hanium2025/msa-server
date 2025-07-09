package hanium.user_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hanium.user_service.repository.MemberRepository;
import io.grpc.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Value("${jwt.expiration}")
    private long accessTokenValidityInSeconds;

    // 사용할 상수 정의
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken"; // 토큰 제목 (sub)
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "email"; // username 클레임
    private static final String BEARER = "Bearer ";
    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final MemberRepository memberRepository;

    // JWT 토큰 생성
    public String generateToken(String email) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT) // 토큰 제목을 "AccessToken"으로 지정
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .withClaim(USERNAME_CLAIM, email) // 클레임 키 "email"에 받아온 email 값 추가
                .sign(Algorithm.HMAC512(secret)); // 지정한 secret 값으로 암호화
    }

    // 토큰에서 사용자 이메일 추출
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(
                    JWT.require(Algorithm.HMAC512(secret))
                            .build()
                            .verify(accessToken)
                            // 검증됐다면 USERNAME_CLAIM, 즉 email 가져옴
                            .getClaim(USERNAME_CLAIM)
                            // 값을 String로 변환
                            .asString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
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
            log.error("유효하지 않은 토큰");
            return false;
        }
    }

    // HTTP 요청에서 토큰 추출
    public Optional<String> extractFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            return Optional.of(authorizationHeader.substring(BEARER.length()));
        }
        return Optional.empty();
    }

    // gRPC 메타데이터에서 토큰 추출
    public Optional<String> extractFromMetadata(Metadata metadata) {
        String authorizationHeader = metadata.get(AUTHORIZATION_METADATA_KEY);
        return extractFromHeader(authorizationHeader);
    }
}
