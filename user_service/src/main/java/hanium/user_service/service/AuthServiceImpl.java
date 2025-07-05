package hanium.user_service.service;

import hanium.user_service.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponseDTO login(String email, String password) {
        return null;
    }
}