package hanium.user_service.service;

import hanium.user_service.dto.MemberSignupRequestDTO;
import hanium.user_service.dto.ResponseMemberDTO;
import hanium.user_service.domain.MemberEntity;

public interface MemberService {

    public ResponseMemberDTO createMember(MemberSignupRequestDTO dto);
    public ResponseMemberDTO getMemberById(Long memberId);
    public MemberEntity getMemberByEmail(String email);
}
