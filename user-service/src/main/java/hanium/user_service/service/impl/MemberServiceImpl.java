package hanium.user_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.domain.Member;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 마이페이지 마케팅 동의 토글
    @Override
    public String toggleAgreeMarketing(Long memberId) {
        Member member = getMemberById(memberId);
        if (member.isAgreeMarketing()) {
            member.updateMarketingStatus(false);
            return "마케팅 정보 수신 동의를 해제하였습니다.";
        } else {
            member.updateMarketingStatus(true);
            return "마케팅 정보 수신에 동의하였습니다.";
        }
    }

    // 마이페이지 제3자 동의 토글
    @Override
    public String toggleAgreeThirdParty(Long memberId) {
        Member member = getMemberById(memberId);
        if (member.isAgreeThirdParty()) {
            member.updateThirdPartyStatus(false);
            return "개인정보 제3자 제공 동의를 해제하였습니다.";
        } else {
            member.updateThirdPartyStatus(true);
            return "개인정보 제3자 제공에 동의하였습니다.";
        }
    }
}
