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
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

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
        return WebClient.create(kakaoUserInfoUri).post()
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
        KakaoUserResponseDTO.KakaoAccount kakaoUser = getKakaoUser(getKakaoAccessToken(code)).getKakaoAccount();
        KakaoUserResponseDTO.KakaoAccount.Profile kakaoProfile = kakaoUser.getProfile();
        Optional<Member> optionalMember = memberRepository.findByEmail(kakaoUser.getEmail());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return proceedKakaoLogin(member);
        } else {
            return kakaoSignupAndLogin(kakaoUser, kakaoProfile);
        }
    }

    // 이미 카카오게정으로 존재하는 회원인 경우 로그인 진행
    private TokenResponseDTO proceedKakaoLogin(Member member) {
        log.info("✅ Kakao Account exists: {}", member.getEmail());
        List<RefreshToken> refreshToken = refreshRepository.findByMember(member);
        if (!refreshToken.isEmpty()) {
            refreshRepository.deleteAll(refreshToken);
        }
        return jwtUtil.respondTokens(member);
    }

    // 신규 카카오계정 로그인인 경우 서비스 회원가입 및 로그인 진행
    private TokenResponseDTO kakaoSignupAndLogin(KakaoUserResponseDTO.KakaoAccount kakaoUser,
                                                 KakaoUserResponseDTO.KakaoAccount.Profile kakaoProfile) {
        log.info("✅ Kakao Account doesn't exist, SignUp processing");

        // Member, Profile 엔티티 생성
        Member member = Member.builder()
                .email(kakaoUser.getEmail()).provider(Provider.KAKAO).role(Role.USER)
                .isAgreeMarketing(false).isAgreeThirdParty(false)
                .build();
        Profile profile = Profile.builder()
                .nickname(extractEmailPart(kakaoUser.getEmail()))
                .imageUrl(kakaoProfile.getProfileImageUrl())
                .member(member).build();
        memberRepository.save(member);
        profileRepository.save(profile);

        log.info("✅ Member id: {}, Profile id: {}", member.getId(), profile.getId());
        log.info("✅ Member provider: {}", member.getProvider());

        return jwtUtil.respondTokens(member);
    }

    // 이메일에서 @ 사인 앞 부분을 반환해 닉네임에 사용
    private String extractEmailPart(String email) {
        int atIndex = email.indexOf('@');
        // '@'가 없으면 원본 그대로 반환
        if (atIndex == -1) {
            return email;
        }
        return email.substring(0, atIndex);
    }
}
