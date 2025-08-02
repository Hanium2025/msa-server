package hanium.user_service.service;

import hanium.user_service.dto.response.KakaoUserResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;

import java.util.Map;

public interface OAuthService {

    Map<String, String> getKakaoConfig();

    String getKakaoAccessToken(String code);

    KakaoUserResponseDTO getKakaoUser(String accessToken);

    TokenResponseDTO kakaoLogin(String code);
}
