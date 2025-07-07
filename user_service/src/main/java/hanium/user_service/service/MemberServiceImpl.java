package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.domain.Profile;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.exception.CustomException;
import hanium.user_service.exception.ErrorCode;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * @param dto 회원 가입 요청
     * @return 회원 가입 응답
     * @apiNote 회원을 생성합니다.
     */
    @Override
    public Member signup(MemberSignupRequestDto dto) {
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

            log.info("Member has been signed up with email: {}", member.getEmail());
            log.info("Profile has been saved: {}", profile.getNickname());

            return member;
        }
    }

    /**
     * @param memberId 회원 ID
     * @return 회원 조회 응답
     * @apiNote 회원 ID로 회원을 조회합니다.
     */
    @Override
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
