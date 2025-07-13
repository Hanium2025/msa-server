package hanium.user_service.service.impl;

import hanium.user_service.domain.Member;
import hanium.user_service.domain.Profile;
import hanium.user_service.domain.Provider;
import hanium.user_service.domain.Role;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.LoginResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.exception.CustomException;
import hanium.user_service.exception.ErrorCode;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.security.JwtUtil;
import hanium.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final ProfileRepository profileRepository;

    @Override
    public Member signUp(SignUpRequestDTO dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) { // 이미 가입된 이메일인가?
            throw new CustomException(ErrorCode.HAS_EMAIL);
        } else if (!dto.getPassword().equals(dto.getConfirmPassword())) { // 비밀번호 확인 필드와 일치하는가?
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        } else {
            // 비밀번호 암호화
            String encodedPassword = encoder.encode(dto.getPassword());

            // Profile 엔티티 생성
            Profile profile = Profile.builder()
                    .nickname(dto.getNickname())
                    .build();

            // Member 엔티티 생성
            Member member = Member.builder()
                    .email(dto.getEmail())
                    .password(encodedPassword)
                    .phoneNumber(dto.getPhoneNumber())
                    .provider(Provider.ORIGINAL)
                    .role(Role.USER)
                    .isAgreeMarketing(dto.getAgreeMarketing())
                    .isAgreeThirdParty(dto.getAgreeThirdParty())
                    .profile(profile)
                    .build();

            memberRepository.save(member);
            profileRepository.save(profile);

            log.info("✅ Member 회원가입 됨: {}", member.getEmail());
            log.info("✅ Profile 등록됨: {} == ID: {}", member.getProfile().getId(), profile.getId());
            log.info("✅ 권한 확인: {}", member.getAuthorities());

            return member;
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        Member member = memberRepository.findByEmail(email)
                .filter(m -> encoder.matches(password, m.getPassword()))
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));
        // 로그인 성공 시 토큰 생성
        String token = jwtUtil.generateToken(member.getEmail());
        return LoginResponseDTO.of(email, token, "Bearer");
    }

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) throws CustomException {
        if (jwtUtil.isTokenValid(refreshToken)) {
            String username = String.valueOf(jwtUtil.extractEmail(refreshToken));
            String newAccessToken = jwtUtil.generateToken(username);
            return new TokenResponseDTO(newAccessToken, refreshToken);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}