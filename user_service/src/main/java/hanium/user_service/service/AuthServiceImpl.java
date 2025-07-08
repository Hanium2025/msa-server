package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.domain.Profile;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.response.LoginResponseDto;
import hanium.user_service.dto.response.TokenResponseDto;
import hanium.user_service.exception.CustomException;
import hanium.user_service.exception.ErrorCode;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.security.common.JwtUtil;
import hanium.user_service.security.service.JwtAuthenticationService;
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
    public Member signUp(MemberSignupRequestDto dto) {
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
                    .isAgreeMarketing(dto.getAgreeMarketing())
                    .isAgreeThirdParty(dto.getAgreeThirdParty())
                    .profile(profile)
                    .build();

            memberRepository.save(member);
            profileRepository.save(profile);

            log.info("✅ Member 회원가입 됨: {}", member.getEmail());
            log.info("✅ Profile 등록됨: {} == ID: {}", member.getProfile().getId(), profile.getId());

            return member;
        }
    }

    @Override
    public LoginResponseDto login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .filter(m -> encoder.matches(password, m.getPassword()))
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));
        // 로그인 성공 시 토큰 생성
        String token = jwtUtil.generateToken(member.getEmail());
        return LoginResponseDto.of(email, token, "Bearer");
    }

    @Override
    public TokenResponseDto refreshToken(String refreshToken) throws CustomException {
        if (jwtUtil.isTokenValid(refreshToken)) {
            String username = String.valueOf(jwtUtil.extractEmail(refreshToken));
            String newAccessToken = jwtUtil.generateToken(username);
            return new TokenResponseDto(newAccessToken, refreshToken);
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}