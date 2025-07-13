package hanium.user_service.dto.request;

import lombok.Getter;

@Getter
public class TokenRefreshRequestDTO {

    private String refreshToken;
}
