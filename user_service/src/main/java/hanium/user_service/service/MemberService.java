package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.response.MemberResponseDto;

public interface MemberService {

    public MemberResponseDto signup(MemberSignupRequestDto dto);

    public MemberResponseDto getMemberById(Long memberId);

    public Member getMemberByEmail(String email);
}
