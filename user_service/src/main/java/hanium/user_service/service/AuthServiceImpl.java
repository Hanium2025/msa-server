package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.response.LoginResponseDto;
import hanium.user_service.dto.response.TokenResponseDto;
import hanium.user_service.exception.CustomException;
import hanium.user_service.exception.ErrorCode;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.security.common.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDto login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));
        // 로그인 성공 시 토큰 생성
        String token = jwtUtil.generateToken(member.getEmail());
        return LoginResponseDto.of(email, token, "Bearer");
    }

    @Override
    public TokenResponseDto refreshToken(String refreshToken) {
        if (jwtUtil.isTokenValid(refreshToken)) {
            String username = String.valueOf(jwtUtil.extractEmail(refreshToken));
            String newAccessToken = jwtUtil.generateToken(username);
            return new TokenResponseDto(newAccessToken, refreshToken);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}