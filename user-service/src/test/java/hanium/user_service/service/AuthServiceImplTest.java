package hanium.user_service.service;

import hanium.common.exception.CustomException;
import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuthServiceImplTest {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private SignUpRequestDTO signupDto;

    @Autowired
    public AuthServiceImplTest(AuthService authService, MemberRepository memberRepository,
                               ProfileRepository profileRepository) {
        this.authService = authService;
        this.memberRepository = memberRepository;
        this.profileRepository = profileRepository;
    }

    @BeforeEach
    void setUp() {
        signupDto = SignUpRequestDTO.builder()
                .email("email@example.com").password("test1234").confirmPassword("test1234")
                .phoneNumber("01012341234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        memberRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    @DisplayName("정상 회원가입")
    void signup() {
        // given: signupDto
        // when
        Member savedMember = authService.signUp(signupDto);
        // then
        assertThat(savedMember.getId()).isNotNull();
    }

    @Test
    @DisplayName("회원가입: 중복 이메일")
    void signup_email() {
        // given
        SignUpRequestDTO duplicate = SignUpRequestDTO.builder()
                .email("email@example.com").password("test1234").confirmPassword("test1234")
                .phoneNumber("010-1234-1234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        // when
        authService.signUp(signupDto);
        CustomException e = assertThrows(CustomException.class, () -> authService.signUp(duplicate));
        // then
        assertThat(e.getErrorCode().name()).isEqualTo("HAS_EMAIL");
    }

    @Test
    @DisplayName("회원가입: 비밀번호 재확인 불일치")
    void signup_password() {
        // given
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .email("email@example.com").password("test1234").confirmPassword("doesn't match")
                .phoneNumber("01012341234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        // when
        CustomException e = assertThrows(CustomException.class, () -> authService.signUp(dto));
        // then
        assertThat(e.getErrorCode().name()).isEqualTo("PASSWORD_NOT_MATCH");
    }

    @Test
    @DisplayName("로그인: 성공")
    void login() {
        // given
        authService.signUp(signupDto);
        LoginRequestDTO dto = LoginRequestDTO.of(signupDto.getEmail(), signupDto.getPassword());
        // when
        TokenResponseDTO result = authService.login(dto);
        // then
        assertThat(result.getEmail()).isEqualTo(signupDto.getEmail());
    }

    @Test
    @DisplayName("로그인: 실패")
    void login_failed() {
        // given
        authService.signUp(signupDto);
        LoginRequestDTO loginDto = LoginRequestDTO.of(signupDto.getEmail(), "wrong1234");
        // when
        CustomException e = assertThrows(CustomException.class, () ->
                authService.login(loginDto));
        // then
        assertThat(e.getErrorCode().name()).isEqualTo("LOGIN_FAILED");
    }
}