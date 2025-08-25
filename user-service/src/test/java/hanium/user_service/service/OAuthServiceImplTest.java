package hanium.user_service.service;

import hanium.user_service.domain.*;
import hanium.user_service.dto.response.KakaoUserResponseDTO;
import hanium.user_service.dto.response.NaverUserResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.repository.RefreshTokenRepository;
import hanium.user_service.service.impl.OAuthServiceImpl;
import hanium.user_service.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("소셜로그인 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OAuthServiceImplTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    RefreshTokenRepository refreshRepository;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    OAuthServiceImpl sut;

    @Test
    @DisplayName("카카오 신규: Member/Profile 저장 후 토큰 발급")
    void kakaoSignupAndLogin_success() {
        // given
        KakaoUserResponseDTO.KakaoAccount kakaoAccount =
                mock(KakaoUserResponseDTO.KakaoAccount.class);
        KakaoUserResponseDTO.KakaoAccount.Profile kakaoProfile =
                mock(KakaoUserResponseDTO.KakaoAccount.Profile.class);

        given(kakaoAccount.getEmail()).willReturn("kakao@example.com");
        given(kakaoProfile.getNickName()).willReturn("카카오 닉네임");
        given(kakaoProfile.getProfileImageUrl()).willReturn("img/kakao.png");

        given(memberRepository.save(any(Member.class)))
                .willAnswer(inv -> inv.getArgument(0));
        given(profileRepository.save(any(Profile.class)))
                .willAnswer(inv -> inv.getArgument(0));

        TokenResponseDTO tokens = mock(TokenResponseDTO.class);
        given(jwtUtil.respondTokens(any(Member.class))).willReturn(tokens);

        // when
        TokenResponseDTO result = sut.kakaoSignupAndLogin(kakaoAccount, kakaoProfile);

        // then
        assertThat(result).isSameAs(tokens);

        // 저장된 객체 캡처 및 검증
        ArgumentCaptor<Member> memberCap = ArgumentCaptor.forClass(Member.class);
        ArgumentCaptor<Profile> profileCap = ArgumentCaptor.forClass(Profile.class);

        then(memberRepository).should(times(1)).save(memberCap.capture());
        then(profileRepository).should(times(1)).save(profileCap.capture());
        Member savedMember = memberCap.getValue();
        Profile savedProfile = profileCap.getValue();

        assertThat(savedMember.getEmail()).isEqualTo("kakao@example.com");
        assertThat(savedMember.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(savedMember.getRole()).isEqualTo(Role.USER);

        assertThat(savedProfile.getNickname()).isEqualTo("카카오 닉네임");
        assertThat(savedProfile.getImageUrl()).isEqualTo("img/kakao.png");
        assertThat(savedProfile.getMember()).isSameAs(savedMember);

        then(jwtUtil).should().respondTokens(savedMember);
    }

    @Test
    @DisplayName("네이버 신규: Member/Profile 저장 후 토큰 발급")
    void naverSignupAndLogin_success() {
        // given
        NaverUserResponseDTO.Response naverUser = mock(NaverUserResponseDTO.Response.class);
        given(naverUser.getEmail()).willReturn("naver@example.com");
        given(naverUser.getMobile()).willReturn("010-1234-5678");
        given(naverUser.getNickname()).willReturn("네이버 닉네임");
        given(naverUser.getProfileImage()).willReturn("img/naver.png");

        given(memberRepository.save(any(Member.class)))
                .willAnswer(inv -> inv.getArgument(0));
        given(profileRepository.save(any(Profile.class)))
                .willAnswer(inv -> inv.getArgument(0));

        TokenResponseDTO tokens = mock(TokenResponseDTO.class);
        given(jwtUtil.respondTokens(any(Member.class))).willReturn(tokens);

        // when
        TokenResponseDTO result = sut.naverSignupAndLogin(naverUser);

        // then
        assertThat(result).isSameAs(tokens);

        ArgumentCaptor<Member> memberCap = ArgumentCaptor.forClass(Member.class);
        ArgumentCaptor<Profile> profileCap = ArgumentCaptor.forClass(Profile.class);

        then(memberRepository).should().save(memberCap.capture());
        then(profileRepository).should().save(profileCap.capture());

        Member savedMember = memberCap.getValue();
        Profile savedProfile = profileCap.getValue();

        assertThat(savedMember.getEmail()).isEqualTo("naver@example.com");
        assertThat(savedMember.getPhoneNumber()).isEqualTo("01012345678"); // 하이픈 제거
        assertThat(savedMember.getProvider()).isEqualTo(Provider.NAVER);
        assertThat(savedMember.getRole()).isEqualTo(Role.USER);

        assertThat(savedProfile.getNickname()).isEqualTo("네이버 닉네임");
        assertThat(savedProfile.getImageUrl()).isEqualTo("img/naver.png");
        assertThat(savedProfile.getMember()).isSameAs(savedMember);

        then(jwtUtil).should().respondTokens(savedMember);
    }

    @Nested
    @DisplayName("기존 소셜 로그인 회원")
    class ProceedSocialLogin {

        @Test
        @DisplayName("RefreshToken 없음")
        void proceedSocialLogin_noExistingRefresh() {
            // given
            Member member = mock(Member.class);
            given(refreshRepository.findByMember(member)).willReturn(List.of());

            TokenResponseDTO tokens = mock(TokenResponseDTO.class);
            given(jwtUtil.respondTokens(member)).willReturn(tokens);

            // when
            TokenResponseDTO result = ReflectionTestUtils.invokeMethod(sut, "proceedSocialLogin", member);

            // then
            assertThat(result).isSameAs(tokens);
            then(refreshRepository).should(never()).deleteAll(anyList());
            then(jwtUtil).should().respondTokens(member);
        }

        @Test
        @DisplayName("RefreshToken 존재: 모두 삭제 후 토큰 발급")
        void proceedSocialLogin_withExistingRefresh() {
            // given
            Member member = mock(Member.class);
            List<RefreshToken> olds = List.of(mock(RefreshToken.class), mock(RefreshToken.class));
            given(refreshRepository.findByMember(member)).willReturn(olds);

            TokenResponseDTO tokens = mock(TokenResponseDTO.class);
            given(jwtUtil.respondTokens(member)).willReturn(tokens);

            // when
            TokenResponseDTO result = ReflectionTestUtils.invokeMethod(sut, "proceedSocialLogin", member);

            // then
            assertThat(result).isSameAs(tokens);
            then(refreshRepository).should().deleteAll(olds);
            then(jwtUtil).should().respondTokens(member);
        }

        @Test
        @DisplayName("이메일로 찾으면 proceedSocialLogin")
        void existingMember_flow_indirect_kakao() {
            Member existing = mock(Member.class);
            given(refreshRepository.findByMember(existing)).willReturn(List.of());
            given(jwtUtil.respondTokens(existing)).willReturn(mock(TokenResponseDTO.class));

            // when
            TokenResponseDTO res = ReflectionTestUtils.invokeMethod(sut, "proceedSocialLogin", existing);

            // then
            assertThat(res).isNotNull();
            then(jwtUtil).should().respondTokens(existing);
        }
    }
}