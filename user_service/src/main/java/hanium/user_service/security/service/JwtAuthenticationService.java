package hanium.user_service.security.service;

import hanium.user_service.domain.Member;
import hanium.user_service.exception.CustomException;
import hanium.user_service.exception.ErrorCode;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.security.common.JwtUtil;
import hanium.user_service.security.token.GrpcAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtAuthenticationService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public Authentication authenticateToken(String token) {
        try {
            log.info("JWT 인증 시작 - 토큰: {}", token);

            // 토큰 유효성 검사
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT 토큰이 유효하지 않음");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // 사용자 이메일 추출
            String email = String.valueOf(jwtUtil.extractEmail(token));
            log.info("토큰에서 추출한 사용자 이메일: {}", email);

            // 사용자 정보 로드
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            return new GrpcAuthenticationToken(
                    member,
                    token,
                    member.getAuthorities()
            );
        } catch (CustomException e) {
            log.error("JWT 인증 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}
