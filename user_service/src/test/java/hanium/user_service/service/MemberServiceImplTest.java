package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.exception.CustomException;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    @DisplayName("정상 회원가입")
    void signup() {
        // given
        MemberSignupRequestDto memberSignupRequestDto = MemberSignupRequestDto.builder()
                .email("email@example.com")
                .password("test1234")
                .confirmPassword("test1234")
                .phoneNumber("010-1234-1234")
                .nickname("nickname")
                .agreeMarketing(true)
                .agreeThirdParty(true)
                .build();
        // when
        Member savedMember = memberService.signup(memberSignupRequestDto);
        // then
        assertThat(savedMember.getId()).isNotNull();
    }

    @Test
    @DisplayName("회원가입: 중복 이메일")
    void signup_email() {
        // given
        MemberSignupRequestDto memberRequest1 = MemberSignupRequestDto.builder()
                .email("email@example.com").password("test1234").confirmPassword("test1234")
                .phoneNumber("010-1234-1234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        MemberSignupRequestDto memberRequest2 = MemberSignupRequestDto.builder()
                .email("email@example.com").password("test1234").confirmPassword("test1234")
                .phoneNumber("010-1234-1234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        // when
        Member member1 = memberService.signup(memberRequest1);
        CustomException e = assertThrows(CustomException.class, () -> memberService.signup(memberRequest2));
        // then
        assertThat(e.getErrorCode().name()).isEqualTo("HAS_EMAIL");
    }

    @Test
    @DisplayName("회원가입: 비밀번호 재확인 불일치")
    void signup_password() {
        // given
        MemberSignupRequestDto dto = MemberSignupRequestDto.builder()
                .email("email@example.com").password("test1234").confirmPassword("doesn't match")
                .phoneNumber("010-1234-1234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        // when
        CustomException e = assertThrows(CustomException.class, () -> memberService.signup(dto));
        // then
        assertThat(e.getErrorCode().name()).isEqualTo("PASSWORD_NOT_MATCH");
    }

    @Test
    @DisplayName("회원가입: 비밀번호 암호화 체크")
    void signup_encode() {
        // given
        MemberSignupRequestDto dto = MemberSignupRequestDto.builder()
                .email("email@example.com").password("test1234").confirmPassword("test1234")
                .phoneNumber("010-1234-1234").nickname("nickname")
                .agreeMarketing(true).agreeThirdParty(true).build();
        // when
        Member member = memberService.signup(dto);
        // then
        assertThat(member.getPassword()).isNotEqualTo("test1234");
    }
}