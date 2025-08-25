package hanium.user_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.domain.*;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.repository.RefreshTokenRepository;
import hanium.user_service.service.AuthService;
import hanium.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Override
    public Member signUp(SignUpRequestDTO dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) { // 이미 가입된 이메일인가?
            throw new CustomException(ErrorCode.HAS_EMAIL);
        } else if (!dto.getPassword().equals(dto.getConfirmPassword())) { // 비밀번호 확인 필드와 일치하는가?
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        } else {
            // 비밀번호 암호화
            String encodedPassword = encoder.encode(dto.getPassword());
            // Member, Profile 엔티티 생성
            Member member = Member.builder()
                    .email(dto.getEmail()).password(encodedPassword)
                    .phoneNumber(dto.getPhoneNumber())
                    .provider(Provider.ORIGINAL).role(Role.USER)
                    .isAgreeMarketing(dto.getAgreeMarketing())
                    .isAgreeThirdParty(dto.getAgreeThirdParty())
                    .build();

            String imageUrl = "https://msa-image-bucket.s3.ap-northeast-2.amazonaws.com/"
                    + "profile_image/default/default_profile.png";
            Profile profile = Profile.builder()
                    .nickname(dto.getNickname())
                    .imageUrl(imageUrl)
                    .member(member).build();

            memberRepository.save(member);
            profileRepository.save(profile);

            log.info("✅ Member added: {}", member.getEmail());
            log.info("✅ Profile added, id: {} for Member id: {}", profile.getId(), member.getId());
            log.info("✅ Member authorities: {}", member.getAuthorities());

            return member;
        }
    }

    @Override
    public TokenResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        Member member = memberRepository.findByEmail(email)
                .filter(m -> encoder.matches(password, m.getPassword()))
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        // 로그인 성공 시 (기존 Refresh 삭제 후) 새 토큰 생성
        List<RefreshToken> refreshToken = refreshRepository.findByMember(member);
        if (!refreshToken.isEmpty()) {
            refreshRepository.deleteAll(refreshToken);
        }
        log.info("✅ Login succeed: {}", member.getId());
        return jwtUtil.respondTokens(member);
    }
}