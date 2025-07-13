//package hanium.user_service.service;
//
//import hanium.user_service.domain.Member;
//import hanium.user_service.dto.request.SignUpRequestDTO;
//import hanium.user_service.exception.CustomException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@Transactional
//class MemberServiceImplTest {
//
//    @Autowired
//    private AuthService authService;
//
//    @Test
//    @DisplayName("정상 회원가입")
//    void signup() {
//        // given
//        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
//                .email("email@example.com")
//                .password("test1234")
//                .confirmPassword("test1234")
//                .phoneNumber("010-1234-1234")
//                .nickname("nickname")
//                .agreeMarketing(true)
//                .agreeThirdParty(true)
//                .build();
//        // when
//        Member savedMember = authService.signUp(signUpRequestDTO);
//        // then
//        assertThat(savedMember.getId()).isNotNull();
//    }
//
//    @Test
//    @DisplayName("회원가입: 중복 이메일")
//    void signup_email() {
//        // given
//        SignUpRequestDTO memberRequest1 = SignUpRequestDTO.builder()
//                .email("email@example.com").password("test1234").confirmPassword("test1234")
//                .phoneNumber("010-1234-1234").nickname("nickname")
//                .agreeMarketing(true).agreeThirdParty(true).build();
//        SignUpRequestDTO memberRequest2 = SignUpRequestDTO.builder()
//                .email("email@example.com").password("test1234").confirmPassword("test1234")
//                .phoneNumber("010-1234-1234").nickname("nickname")
//                .agreeMarketing(true).agreeThirdParty(true).build();
//        // when
//        Member member1 = authService.signUp(memberRequest1);
//        CustomException e = assertThrows(CustomException.class, () -> authService.signUp(memberRequest2));
//        // then
//        assertThat(e.getErrorCode().name()).isEqualTo("HAS_EMAIL");
//    }
//
//    @Test
//    @DisplayName("회원가입: 비밀번호 재확인 불일치")
//    void signup_password() {
//        // given
//        SignUpRequestDTO dto = SignUpRequestDTO.builder()
//                .email("email@example.com").password("test1234").confirmPassword("doesn't match")
//                .phoneNumber("010-1234-1234").nickname("nickname")
//                .agreeMarketing(true).agreeThirdParty(true).build();
//        // when
//        CustomException e = assertThrows(CustomException.class, () -> authService.signUp(dto));
//        // then
//        assertThat(e.getErrorCode().name()).isEqualTo("PASSWORD_NOT_MATCH");
//    }
//
//    @Test
//    @DisplayName("회원가입: 비밀번호 암호화 체크")
//    void signup_encode() {
//        // given
//        SignUpRequestDTO dto = SignUpRequestDTO.builder()
//                .email("email@example.com").password("test1234").confirmPassword("test1234")
//                .phoneNumber("010-1234-1234").nickname("nickname")
//                .agreeMarketing(true).agreeThirdParty(true).build();
//        // when
//        Member member = authService.signUp(dto);
//        // then
//        assertThat(member.getPassword()).isNotEqualTo("test1234");
//    }
//}