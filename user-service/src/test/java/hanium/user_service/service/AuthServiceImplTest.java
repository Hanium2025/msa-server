package hanium.user_service.service;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.domain.*;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.repository.RefreshTokenRepository;
import hanium.user_service.service.impl.AuthServiceImpl;
import hanium.user_service.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("Auth 서비스 테스트")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceImplTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    RefreshTokenRepository refreshRepository;
    @Mock
    BCryptPasswordEncoder encoder;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthServiceImpl sut;

    // 회원가입 요청 dto mock
    private SignUpRequestDTO mockSignUpDto(
            String email, String pw, String confirmPw,
            String phone, String nickname,
            Boolean agreeMarketing, Boolean agreeThirdParty
    ) {
        SignUpRequestDTO dto = mock(SignUpRequestDTO.class);
        given(dto.getEmail()).willReturn(email);
        given(dto.getPassword()).willReturn(pw);
        given(dto.getConfirmPassword()).willReturn(confirmPw);
        given(dto.getPhoneNumber()).willReturn(phone);
        given(dto.getNickname()).willReturn(nickname);
        given(dto.getAgreeMarketing()).willReturn(agreeMarketing);
        given(dto.getAgreeThirdParty()).willReturn(agreeThirdParty);
        return dto;
    }

    // 로그인 요청 dto mock
    private LoginRequestDTO mockLoginDto(String email, String password) {
        LoginRequestDTO dto = mock(LoginRequestDTO.class);
        given(dto.getEmail()).willReturn(email);
        given(dto.getPassword()).willReturn(password);
        return dto;
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("성공: 비밀번호 인코딩, Member/Profile 저장, 필드값 검증")
        void signUp_success() {
            // given
            String email = "user@example.com";
            String rawPw = "password";
            String encPw = "encoded";
            String phone = "01012345678";
            String nickname = "피키";
            boolean agreeMkt = true;
            boolean agree3rd = false;

            SignUpRequestDTO dto = mockSignUpDto(email, rawPw, rawPw, phone, nickname, agreeMkt, agree3rd);

            given(memberRepository.findByEmail(email)).willReturn(Optional.empty());
            given(encoder.encode(rawPw)).willReturn(encPw);
            given(memberRepository.save(any(Member.class)))
                    .willAnswer(inv -> inv.getArgument(0));
            given(profileRepository.save(any(Profile.class)))
                    .willAnswer(inv -> inv.getArgument(0));

            // when
            Member result = sut.signUp(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getPassword()).isEqualTo(encPw);
            assertThat(result.getProvider()).isEqualTo(Provider.ORIGINAL);
            assertThat(result.getRole()).isEqualTo(Role.USER);

            then(memberRepository).should(times(1)).save(any(Member.class));
            ArgumentCaptor<Profile> profileCap = ArgumentCaptor.forClass(Profile.class);
            then(profileRepository).should(times(1)).save(profileCap.capture());
            assertThat(profileCap.getValue().getMember()).isSameAs(result);

            then(encoder).should(times(1)).encode(rawPw);
            then(memberRepository).should(times(1)).findByEmail(email);
            then(jwtUtil).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패: 이미 가입된 이메일이면 HAS_EMAIL 예외")
        void signUp_emailExists_throws() {
            // given
            String email = "user@example.com";
            SignUpRequestDTO dto = mock(SignUpRequestDTO.class);
            given(dto.getEmail()).willReturn(email);
            given(memberRepository.findByEmail(email)).willReturn(Optional.of(mock(Member.class)));

            // when, then
            assertThatThrownBy(() -> sut.signUp(dto))
                    .isInstanceOfSatisfying(CustomException.class, ex ->
                            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.HAS_EMAIL));

            then(encoder).shouldHaveNoInteractions();
            then(memberRepository).should(never()).save(any());
            then(profileRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패: 비밀번호 불일치면 PASSWORD_NOT_MATCH 예외")
        void signUp_passwordMismatch_throws() {
            // given
            String email = "user@example.com";
            SignUpRequestDTO dto = mock(SignUpRequestDTO.class);
            given(dto.getEmail()).willReturn(email);
            given(dto.getPassword()).willReturn("pw1");
            given(dto.getConfirmPassword()).willReturn("pw2");

            given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.signUp(dto))
                    .isInstanceOfSatisfying(CustomException.class, ex ->
                            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_NOT_MATCH));

            then(encoder).shouldHaveNoInteractions();
            then(memberRepository).should(never()).save(any());
            then(profileRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공: 기존 Refresh 없으면 삭제 없이 토큰 발급")
        void login_success_noExistingRefresh() {
            // given
            String email = "login@example.com";
            String rawPw = "pw!";
            String encPw = "ENC";
            LoginRequestDTO dto = mockLoginDto(email, rawPw);

            Member member = mock(Member.class);
            given(member.getPassword()).willReturn(encPw);
            given(member.getId()).willReturn(42L);

            given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
            given(encoder.matches(rawPw, encPw)).willReturn(true);
            given(refreshRepository.findByMember(member)).willReturn(List.of());

            TokenResponseDTO tokens = mock(TokenResponseDTO.class);
            given(jwtUtil.respondTokens(member)).willReturn(tokens);

            // when
            TokenResponseDTO result = sut.login(dto);

            // then
            assertThat(result).isSameAs(tokens);
            then(refreshRepository).should(never()).deleteAll(anyList());
            then(jwtUtil).should(times(1)).respondTokens(member);
        }

        @Test
        @DisplayName("성공: 기존 Refresh 있으면 모두 삭제 후 토큰 발급")
        void login_success_withExistingRefresh() {
            // given
            String email = "login@example.com";
            String rawPw = "pw!";
            String encPw = "ENC";
            LoginRequestDTO dto = mockLoginDto(email, rawPw);

            Member member = mock(Member.class);
            given(member.getPassword()).willReturn(encPw);
            given(member.getId()).willReturn(1L);

            given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
            given(encoder.matches(rawPw, encPw)).willReturn(true);

            List<RefreshToken> olds = List.of(mock(RefreshToken.class), mock(RefreshToken.class));
            given(refreshRepository.findByMember(member)).willReturn(olds);

            TokenResponseDTO tokens = mock(TokenResponseDTO.class);
            given(jwtUtil.respondTokens(member)).willReturn(tokens);

            // when
            TokenResponseDTO result = sut.login(dto);

            // then
            assertThat(result).isSameAs(tokens);
            then(refreshRepository).should(times(1)).deleteAll(olds);
            then(jwtUtil).should(times(1)).respondTokens(member);
        }

        @Test
        @DisplayName("실패: 이메일 없거나 비밀번호 불일치면 LOGIN_FAILED 예외")
        void login_fail_emailNotFound_or_passwordMismatch() {
            // case1: 이메일 없음
            LoginRequestDTO dto1 = mockLoginDto("none@example.com", "pw");
            given(memberRepository.findByEmail("none@example.com")).willReturn(Optional.empty());

            assertThatThrownBy(() -> sut.login(dto1))
                    .isInstanceOfSatisfying(CustomException.class,
                            ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED));

            // case2: 이메일은 있으나 패스워드 불일치
            LoginRequestDTO dto2 = mockLoginDto("user@example.com", "bad");
            Member member = mock(Member.class);
            given(member.getPassword()).willReturn("ENC");
            given(memberRepository.findByEmail("user@example.com")).willReturn(Optional.of(member));
            given(encoder.matches("bad", "ENC")).willReturn(false);

            assertThatThrownBy(() -> sut.login(dto2))
                    .isInstanceOfSatisfying(CustomException.class,
                            ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED));

            then(jwtUtil).shouldHaveNoInteractions();
            then(refreshRepository).should(never()).deleteAll(anyList());
        }
    }
}