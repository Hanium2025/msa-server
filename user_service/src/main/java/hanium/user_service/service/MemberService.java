package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {

    public Member getMemberById(Long memberId);
}
