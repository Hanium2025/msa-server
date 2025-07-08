package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDto;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.response.LoginResponseDto;
import hanium.user_service.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@Transactional
class AuthServiceImplTest {

    @Autowired
    private AuthService authService;
    private MemberSignupRequestDto signupDto;

    @BeforeEach
    void setUp() {
        signupDto = MemberSignupRequestDto.builder()
                .email("email@example.com").password("test1234").confirmPassword("test1234")
                .phoneNumber("010-1234-1234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
    }

    @Test
    @DisplayName("로그인: 성공")
    void login() {
        // given
        Member member = authService.signUp(signupDto);
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email("email@example.com").password("test1234").build();
        // when
        LoginResponseDto response = authService.login(loginDto.getEmail(), loginDto.getPassword());
        // then
        assertThat(response.getEmail()).isEqualTo(loginDto.getEmail());
        assertThat(response.getAccessToken()).isNotNull();
        log.info("로그인 성공, 토큰: {}", response.getAccessToken());
    }

    @Test
    @DisplayName("로그인: 실패")
    void login_failed() {
        // given
        Member member = authService.signUp(signupDto);
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email("email@example.com").password("wrongPassword").build();
        // when
        CustomException e = assertThrows(CustomException.class, () ->
                authService.login(loginDto.getEmail(), loginDto.getPassword()));
        // then
        assertThat(e.getErrorCode().name()).isEqualTo("LOGIN_FAILED");
    }

    @Test
    void refreshToken() {
    }
}