package hanium.user_service.service;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.TokenResponseDTO;

public interface AuthService {

    Member signUp(SignUpRequestDTO dto);

    TokenResponseDTO login(LoginRequestDTO dto);

    void sendSms(String phoneNumber);
}