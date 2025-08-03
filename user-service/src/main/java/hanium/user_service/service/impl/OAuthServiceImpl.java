package hanium.user_service.service.impl;

import hanium.user_service.domain.*;
import hanium.user_service.dto.response.*;
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

import java.math.BigInteger;
import java.security.SecureRandom;
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

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;


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
     * application.yml에 설정된 네이버 로그인 client id와 redirect uri, state를 반환합니다.
     *
     * @return NaverConfigResponseDTO
     */
    @Override
    public NaverConfigResponseDTO getNaverConfig() {
        String state = generateState();
        return new NaverConfigResponseDTO(naverClientId, naverRedirectUri, state);
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
            return proceedSocialLogin(member);
        } else {
            return kakaoSignupAndLogin(kakaoUser, kakaoProfile);
        }
    }

    /**
     * 클라이언트에서 네이버 로그인 후 redirect로 전달되는 네이버 인가 코드를 이용해
     * 사용자를 네이버 Provider로 회원가입 후 로그인, 이미 가입되었다면 로그인시키고
     * 토큰을 반환합니다.
     *
     * @param code 네이버 인가 코드
     * @return 로그인 성공한 회원 이메일, Access 및 Refresh 토큰
     */
    @Override
    @Transactional
    public TokenResponseDTO naverLogin(String code) {
        NaverUserResponseDTO.Response naverUser = getNaverUser(getNaverAccessToken(code)).getNaverAccount();
        Optional<Member> optionalMember = memberRepository.findByEmail(naverUser.getEmail());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return proceedSocialLogin(member);
        } else {
            return naverSignupAndLogin(naverUser);
        }
    }

    /**
     * 카카오 인가코드를 이용해 카카오 Access 토큰을 얻어 반환합니다.
     *
     * @param code 카카오 로그인 시 생성되는 인가 코드
     * @return 인가 코드로 얻은 카카오 Access 토큰
     */
    private String getKakaoAccessToken(String code) {
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
     * 네이버 인가코드를 이용해 네이버 Access 토큰을 얻어 반환합니다.
     *
     * @param code 네이버 로그인 시 생성되는 인가 코드
     * @return 인가 코드로 얻은 네이버 Access 토큰
     */
    private String getNaverAccessToken(String code) {
        NaverResponseDTO naverResponse = WebClient.create(naverTokenUri).get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", naverClientId)
                        .queryParam("code", code)
                        .queryParam("client_secret", naverClientSecret)
                        .build(true))
                .retrieve().bodyToMono(NaverResponseDTO.class).block();
        return Objects.requireNonNull(naverResponse).getAccessToken();
    }

    /**
     * 카카오 Access 토큰을 이용해 카카오 계정 사용자 정보를 반환합니다.
     *
     * @param accessToken 카카오 Access 토큰
     * @return 카카오 계정 사용자 정보 (KakaoAccount와 KakaoAccount.Profile 등)
     */
    private KakaoUserResponseDTO getKakaoUser(String accessToken) {
        return WebClient.create(kakaoUserInfoUri).post()
                .uri(uriBuilder -> uriBuilder.build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve().bodyToMono(KakaoUserResponseDTO.class).block();
    }

    /**
     * 네이버 Access 토큰을 이용해 네이버 계정 사용자 정보를 반환합니다.
     *
     * @param accessToken 네이버 Access 토큰
     * @return 네이버 계정 사용자 정보
     */
    private NaverUserResponseDTO getNaverUser(String accessToken) {
        return WebClient.create(naverUserInfoUri).get()
                .uri(uriBuilder -> uriBuilder.build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve().bodyToMono(NaverUserResponseDTO.class).block();
    }

    /**
     * 이미 소셜 로그인으로 회원가입된 사용자의 경우, 로그인만 계속 진행합니다.
     *
     * @param member 회원
     * @return 로그인 결과 토큰
     */
    private TokenResponseDTO proceedSocialLogin(Member member) {
        log.info("✅ Social Account [{}] exists: {}", member.getProvider(), member.getEmail());
        List<RefreshToken> refreshToken = refreshRepository.findByMember(member);
        if (!refreshToken.isEmpty()) {
            refreshRepository.deleteAll(refreshToken);
        }
        return jwtUtil.respondTokens(member);
    }

    /**
     * 카카오 계정으로 처음 회원가입하는 사용자의 경우, 서비스 회원가입 및 로그인을 진행합니다.
     *
     * @param kakaoUser    카카오 계정 정보
     * @param kakaoProfile 카카오 프로필 정보
     * @return 로그인 결과 토큰
     */
    private TokenResponseDTO kakaoSignupAndLogin(KakaoUserResponseDTO.KakaoAccount kakaoUser,
                                                 KakaoUserResponseDTO.KakaoAccount.Profile kakaoProfile) {
        Member member = Member.builder()
                .email(kakaoUser.getEmail()).provider(Provider.KAKAO).role(Role.USER)
                .isAgreeMarketing(false).isAgreeThirdParty(false)
                .build();
        Profile profile = Profile.builder()
                .nickname(kakaoProfile.getNickName())
                .imageUrl(kakaoProfile.getProfileImageUrl())
                .member(member).build();
        memberRepository.save(member);
        profileRepository.save(profile);
        return jwtUtil.respondTokens(member);
    }

    /**
     * 네이버 계정으로 처음 회원가입하는 사용자의 경우, 서비스 회원가입 및 로그인을 진행합니다.
     *
     * @param naverUser 네이버 계정 정보
     * @return 로그인 결과 토큰
     */
    private TokenResponseDTO naverSignupAndLogin(NaverUserResponseDTO.Response naverUser) {
        Member member = Member.builder()
                .email(naverUser.getEmail())
                .phoneNumber(naverUser.getMobile().replaceAll("\\D+", ""))
                .provider(Provider.NAVER).role(Role.USER)
                .isAgreeMarketing(false).isAgreeThirdParty(false)
                .build();
        Profile profile = Profile.builder()
                .nickname(naverUser.getNickname())
                .imageUrl(naverUser.getProfileImage())
                .member(member).build();
        memberRepository.save(member);
        profileRepository.save(profile);
        return jwtUtil.respondTokens(member);
    }

    /**
     * 네이버 로그인에서 사용할 state 값을 생성합니다.
     *
     * @return 생성된 state 문자열
     */
    private String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
