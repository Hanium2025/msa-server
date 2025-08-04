package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.response.KakaoUserResponseDTO;
import hanium.user_service.dto.response.NaverUserResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OAuthServiceImplTest {

    private final OAuthService oAuthService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private KakaoUserResponseDTO.KakaoAccount kakaoAccount;
    private NaverUserResponseDTO.Response naverAccount;

    @Autowired
    public OAuthServiceImplTest(OAuthService oAuthService, MemberRepository memberRepository,
                                RefreshTokenRepository refreshTokenRepository) {
        this.oAuthService = oAuthService;
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @BeforeEach
    void setUp() {
        KakaoUserResponseDTO.KakaoAccount.Profile profile = KakaoUserResponseDTO.KakaoAccount.Profile.builder()
                .nickName("카카오닉네임").profileImageUrl("/path/kakao_profile_image").build();
        kakaoAccount = KakaoUserResponseDTO.KakaoAccount.builder()
                .email("email@kakao.com").profile(profile).build();
        naverAccount = NaverUserResponseDTO.Response.builder()
                .email("email@naver.com").nickname("네이버닉네임").mobile("010-6789-2345")
                .profileImage("/path/naver_profile_image").build();
    }

    @Test
    @DisplayName("카카오 계정으로 신규 회원가입")
    void kakaoSignupAndLogin() {
        // when
        TokenResponseDTO dto = oAuthService.kakaoSignupAndLogin(kakaoAccount, kakaoAccount.getProfile());
        Member member = memberRepository.findByEmail(kakaoAccount.getEmail()).get();
        // then
        assertThat(refreshTokenRepository.findByMember(member).getFirst().getToken())
                .isEqualTo(dto.getRefreshToken());
        assertThat(member.getProvider().toString())
                .isEqualTo("KAKAO");
    }

    @Test
    @DisplayName("네이버 계정으로 신규 회원가입")
    void naverSignupAndLogin() {
        // when
        TokenResponseDTO dto = oAuthService.naverSignupAndLogin(naverAccount);
        Member member = memberRepository.findByEmail(naverAccount.getEmail()).get();
        // then
        assertThat(refreshTokenRepository.findByMember(member).getFirst().getToken())
                .isEqualTo(dto.getRefreshToken());
        assertThat(member.getProvider().toString())
                .isEqualTo("NAVER");
        assertThat(member.getPhoneNumber()).isEqualTo("01067892345");
    }
}