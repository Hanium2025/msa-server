package hanium.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDto {

    private String email;
    private String accessToken;
    private String tokenType;

    public static LoginResponseDto of(String loginSuccessEmail, String token, String bearer) {
        return new LoginResponseDto(loginSuccessEmail, token, bearer); // email, accessToken, tokenType
    }
}