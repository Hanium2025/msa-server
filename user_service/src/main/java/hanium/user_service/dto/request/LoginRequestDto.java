package hanium.user_service.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {

    private String email;
    private String password;
}
