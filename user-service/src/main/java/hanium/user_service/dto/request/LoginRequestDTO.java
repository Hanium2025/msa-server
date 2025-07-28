package hanium.user_service.dto.request;

import hanium.common.proto.user.LoginRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequestDTO {

    private String email;
    private String password;

    public static LoginRequestDTO from(LoginRequest request) {
        return LoginRequestDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public static LoginRequestDTO of(String email, String password) {
        return LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();
    }
}
