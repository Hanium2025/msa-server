package hanium.user_service.service;

import hanium.user_service.dto.response.KakaoUserResponseDTO;
import hanium.user_service.dto.response.NaverConfigResponseDTO;
import hanium.user_service.dto.response.NaverUserResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;

import java.util.Map;

public interface OAuthService {

    Map<String, String> getKakaoConfig();

    NaverConfigResponseDTO getNaverConfig();

    TokenResponseDTO kakaoLogin(String code);

    TokenResponseDTO kakaoSignupAndLogin(KakaoUserResponseDTO.KakaoAccount kakaoUser,
                                         KakaoUserResponseDTO.KakaoAccount.Profile kakaoProfile);

    TokenResponseDTO naverLogin(String code);

    TokenResponseDTO naverSignupAndLogin(NaverUserResponseDTO.Response naverUser);
}
