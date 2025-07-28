package hanium.user_service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDTO {

    private String email;
    private String accessToken;
    private String refreshToken;

    public static TokenResponseDTO of(String email, String accessToken, String refreshToken) {
        return TokenResponseDTO.builder()
                .email(email).accessToken(accessToken).refreshToken(refreshToken)
                .build();
    }
}
