package hanium.user_service.service.impl;

import hanium.user_service.domain.*;
import hanium.user_service.dto.response.KakaoResponseDTO;
import hanium.user_service.dto.response.KakaoUserResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.repository.RefreshTokenRepository;
import hanium.user_service.service.OAuthService;
import hanium.user_service.util.JwtUtil;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshRepository;
    private final JwtUtil jwtUtil;

    /**
     * application.yml에 설정된 카카오 로그인 client id와 redirect uri를 반환합니다.
     *
     * @return Map 형태의 kakaoClientId, kakaoRedirectUri
     */
    @Override
    public Map<String, String> getKakaoConfig() {
        return Map.of(
                "kakaoClientId", kakaoClientId,
                "kakaoRedirectUri", kakaoRedirectUri
        );
    }

    /**
     * 카카오 인가코드를 이용해 카카오 Access 토큰을 얻어 반환합니다.
     *
     * @param code 카카오 로그인 시 생성되는 인가 코드
     * @return 인가 코드로 얻은 카카오 Access 토큰
     */
    @Override
    public String getKakaoAccessToken(String code) {
        String kakaoTokenUrl = "https://kauth.kakao.com/oauth/token";
        KakaoResponseDTO kakaoResponse = WebClient.create(kakaoTokenUrl).post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("redirect_uri", kakaoRedirectUri)
                        .queryParam("code", code)
                        .queryParam("client_secret", kakaoClientSecret)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve().bodyToMono(KakaoResponseDTO.class).block();
        return Objects.requireNonNull(kakaoResponse).getAccessToken();
    }

    /**
     * 카카오 Access 토큰을 이용해 카카오 계정 사용자 정보를 반환합니다.
     *
     * @param accessToken 카카오 Access 토큰
     * @return 카카오 계정 사용자 정보 (KakaoAccount와 KakaoAccount.Profile 등)
     */
    @Override
    public KakaoUserResponseDTO getKakaoUser(String accessToken) {
        String kakaoUserUrl = "https://kapi.kakao.com/v2/user/me";
        return WebClient.create(kakaoUserUrl).post()
                .uri(uriBuilder -> uriBuilder.build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve().bodyToMono(KakaoUserResponseDTO.class).block();
    }

    /**
     * 클라이언트에서 카카오로그인 후 redirect로 전달되는 카카오 인가 코드를 이용해
     * 사용자를 카카오 Provider로 회원가입 후 로그인, 이미 가입되었다면 로그인시키고
     * 토큰을 반환합니다.
     *
     * @param code 카카오 인가 코드
     * @return 로그인 성공한 회원 이메일, Access 및 Refresh 토큰
     */
    @Override
    @Transactional
    public TokenResponseDTO kakaoLogin(String code) {
        log.info("✅ Kakao Code sent to OAuthService: {}", code);

        KakaoUserResponseDTO.KakaoAccount kakaoUser = getKakaoUser(getKakaoAccessToken(code)).getKakaoAccount();
        KakaoUserResponseDTO.KakaoAccount.Profile kakaoProfile = kakaoUser.getProfile();

        Optional<Member> optionalMember = memberRepository.findByEmail(kakaoUser.getEmail());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            log.info("✅ Kakao Account exists: {}", member.getEmail());
            List<RefreshToken> refreshToken = refreshRepository.findByMember(member);
            if (!refreshToken.isEmpty()) {
                refreshRepository.deleteAll(refreshToken);
            }
            log.info("✅ Login succeed: {}", member.getEmail());
            return jwtUtil.respondTokens(member);

        } else {
            log.info("✅ Kakao Account doesn't exist, SignUp processing");

            // Member, Profile 엔티티 생성
            Member member = Member.builder()
                    .email(kakaoUser.getEmail()).phoneNumber(kakaoUser.getPhoneNumber())
                    .provider(Provider.KAKAO).role(Role.USER)
                    .isAgreeMarketing(false).isAgreeThirdParty(false)
                    .build();
            Profile profile = Profile.builder()
                    .nickname(kakaoProfile.getNickName())
                    .imageUrl(kakaoProfile.getProfileImageUrl())
                    .member(member).build();
            memberRepository.save(member);
            profileRepository.save(profile);

            log.info("✅ Member added: {}", member.getEmail());
            log.info("✅ Profile added, id: {} for Member id: {}", profile.getId(), member.getId());
            log.info("✅ Member authorities: {}", member.getAuthorities());
            log.info("✅ Member provider: {}", member.getProvider());

            return jwtUtil.respondTokens(member);
        }
    }
}
