package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.TokenResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDTO {

    public String email;
    public String accessToken;
    public String refreshToken;

    public static TokenResponseDTO from(TokenResponse tokenResponse) {
        return TokenResponseDTO.builder()
                .email(tokenResponse.getEmail())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }
}
