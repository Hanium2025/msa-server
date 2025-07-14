package hanium.user_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    // Access Token 토큰 생성
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
}
