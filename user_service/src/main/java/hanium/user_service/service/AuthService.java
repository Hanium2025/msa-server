package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.response.LoginResponseDto;
import hanium.user_service.dto.response.TokenResponseDto;

public interface AuthService {

    public Member signUp(MemberSignupRequestDto dto);

    public LoginResponseDto login(String email, String password);

    public TokenResponseDto refreshToken(String refreshToken);
}