package hanium.user_service.jwt;

import hanium.user_service.domain.MemberEntity;
import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtAuthenticationService {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    public Authentication authenticateToken(String token) {
        try {
            log.info("JWT 인증 시작 - 토큰: {}", token);

            // 토큰 유효성 검사
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT 토큰이 유효하지 않음");
                throw new JwtAuthenticationException("유효하지 않은 JWT 토큰");
            }

            // 사용자 이름 추출
            String email = String.valueOf(jwtUtil.extractEmail(token));
            log.info("토큰에서 추출한 사용자 이름: {}", email);

            // 사용자 정보 로드
            MemberEntity member = memberService.getMemberByEmail(email);

            return new GrpcAuthenticationToken(
                    member,
                    token,
                    member.getAuthorities()
            );
//        } catch (JwtAuthenticationException e) {
//            log.error("JWT 인증 실패: {}", e.getMessage());
//            throw e;
        } catch (Exception e) {
            log.error("JWT 인증 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("JWT 인증 중 오류 발생", e);
        }
    }
}
