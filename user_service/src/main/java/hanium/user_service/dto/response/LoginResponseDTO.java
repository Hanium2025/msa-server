package hanium.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDTO {

    private String email;
    private String accessToken;
    private String tokenType;

    public static LoginResponseDTO of(String loginSuccessEmail, String token, String bearer) {
        return new LoginResponseDTO(loginSuccessEmail, token, bearer); // email, accessToken, tokenType
    }
}