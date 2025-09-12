package hanium.user_service.service;

import hanium.user_service.domain.Member;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {

    Member getMemberById(Long memberId);

    Member getMemberByEmail(String email);

    String toggleAgreeMarketing(Long memberId);

    String toggleAgreeThirdParty(Long memberId);
}
