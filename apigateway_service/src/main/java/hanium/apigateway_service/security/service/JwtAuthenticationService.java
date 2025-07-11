package hanium.apigateway_service.security.service;

import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.security.JwtUtil;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.user.GetAuthorityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtAuthenticationService {

    private final JwtUtil jwtUtil;
    private final UserGrpcClient userGrpcClient;

    /**
     * JWT 토큰을 검증하는 메서드
     * @param token 검증할 토큰
     * @return Authentication 객체
     */
    public Authentication authenticateToken(String token) throws Exception {
        try {
            // 토큰 유효성 검사
            if (!jwtUtil.isTokenValid(token)) {
                log.error("⚠️ authenticateToken: JWT 토큰이 유효하지 않습니다.");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // 사용자 이메일 추출
            String email = jwtUtil.extractEmail(token)
                            .orElseThrow(() -> new Exception("⚠️ 이메일 추출에 실패했습니다."));
            log.info("✅ 토큰에서 추출한 사용자 이메일: {}", email);

            // 이메일로 사용자 권한 정보 가져오기
            GetAuthorityResponse protoResponse = userGrpcClient.getAuthority(email);
            String authority = protoResponse.getAuthority();
            log.info("✅ 가져온 String authority: {}", authority);

            // UsernamePasswordAuthenticationToken에 들어갈
            // Collection<? extends GrantedAuthority> authorities 생성
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(authority));

            return new UsernamePasswordAuthenticationToken(
                    email, token, authorities
            );

        } catch (Exception e) {
            log.error("⚠️ authenticateToken - JWT 인증 실패: {}", e.getMessage());
            throw e;
        }
    }
}
