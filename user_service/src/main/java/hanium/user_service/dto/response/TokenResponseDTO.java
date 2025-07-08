package hanium.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String message;

    public TokenResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = "토큰이 성공적으로 생성되었습니다.";
    }
}
