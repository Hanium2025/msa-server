package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;

public interface MemberService {

    public Member signup(MemberSignupRequestDto dto);

    public Member getMemberById(Long memberId);

    public Member getMemberByEmail(String email);
}
