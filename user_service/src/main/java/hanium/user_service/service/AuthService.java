package hanium.user_service.service;

import hanium.user_service.dto.LoginResponseDTO;

public interface AuthService {

    public LoginResponseDTO login(String email, String password);
}