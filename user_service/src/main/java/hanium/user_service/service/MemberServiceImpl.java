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
