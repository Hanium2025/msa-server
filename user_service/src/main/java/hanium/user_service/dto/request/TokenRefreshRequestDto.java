package hanium.user_service.dto.request;

import lombok.Getter;

@Getter
public class TokenRefreshRequestDto {

    private String refreshToken;
}
