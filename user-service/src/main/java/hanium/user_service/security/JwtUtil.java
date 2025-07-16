package hanium.user_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.domain.Member;
import hanium.user_service.domain.RefreshToken;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
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

    private final RefreshTokenRepository refreshRepository;


    /**
     * Access 토큰을 생성합니다.
     *
     * @param email 사용자 이메일
     * @return 생성한 토큰 String
     */
    public String createAccessToken(String email) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN) // 토큰 제목을 "AccessToken"으로 지정
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .withClaim(EMAIL_CLAIM, email) // 클레임 키 "email"에 받아온 email 값 추가
                .sign(Algorithm.HMAC512(secret)); // 지정한 secret 값으로 암호화
    }

    /**
     * Refresh 토큰을 생성합니다.
     *
     * @return 생성한 토큰 String
     */
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 새 Access, Refresh 토큰을 생성한 후 Refresh 토큰은 데이터베이스에 저장합니다.
     * 생성된 토큰들과 해당 사용자 이메일 정보를 반환합니다.
     *
     * @param member 토큰 생성 대상 사용자
     * @return (사용자 이메일, Access, Refresh 토큰) dto
     */
    public TokenResponseDTO respondTokens(Member member) {
        // 새 토큰 생성
        String accessToken = createAccessToken(member.getEmail());
        String refreshToken = createRefreshToken();

        // Refresh 토큰 저장
        RefreshToken refreshEntity = RefreshToken.builder()
                .token(refreshToken)
                .expiresAt(extractExpiration(refreshToken))
                .member(member).build();
        refreshRepository.save(refreshEntity);

        // 응답 생성
        return TokenResponseDTO.of(member.getEmail(), accessToken, refreshToken);
    }

    /**
     * Refresh 토큰이 데이터베이스에 존재하는지 확인하고 기존 토큰을 삭제합니다.
     * 새 Access, Refresh 토큰 생성해 응답을 반환합니다.
     *
     * @param refreshToken 전달된 Refresh 토큰
     * @return (사용자 이메일, Access, Refresh 토큰) dto
     */
    public TokenResponseDTO checkRefreshTokenAndReissue(String refreshToken) {
        RefreshToken refreshEntity = refreshRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_NOT_FOUND));

        // 기존 Refresh 토큰 삭제
        refreshRepository.delete(refreshEntity);

        // 유효성 검사
        if (isTokenExpired(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 새 토큰으로 응답 생성
        Member member = refreshEntity.getMember();
        return respondTokens(member);
    }

    /**
     * 토큰의 Claim 중 만료 일시를 추출해 LocalDateTime로 변환 후 반환합니다.
     *
     * @param token 전달된 토큰
     * @return 만료 일시
     */
    public LocalDateTime extractExpiration(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(secret)).build().verify(token)
                    .getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 토큰의 유효성을 검증해 여부를 반환합니다.
     *
     * @param token 전달된 토큰
     * @return 만료된 토큰인가? (true/false)
     */
    public boolean isTokenExpired(String token) {
        try {
            /*
             * require: HMAC512 알고리즘과 secret을 사용해 토큰 서명을 검증하도록 설정
             * build:   JWT 검증기 생성
             * verify:  파라미터의 token 검증
             */
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return false;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰");
            return true;
        }
    }
}
