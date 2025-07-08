package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.LoginResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;

public interface AuthService {

    public Member signUp(SignUpRequestDTO dto);

    public LoginResponseDTO login(String email, String password);

    public TokenResponseDTO refreshToken(String refreshToken);
}