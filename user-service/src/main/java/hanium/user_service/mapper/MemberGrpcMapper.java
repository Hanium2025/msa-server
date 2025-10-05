package hanium.user_service.mapper;

import hanium.common.proto.product.SimpleProductResponse;
import hanium.common.proto.user.*;
import hanium.user_service.dto.response.*;

import java.util.Map;

public class MemberGrpcMapper {

    // 회원가입 응답 dto -> gRPC
    public static SignUpResponse toSignupResponse(SignUpResponseDTO dto) {
        return SignUpResponse.newBuilder()
                .setMemberId(dto.getId())
                .setEmail(dto.getEmail())
                .setPhoneNumber(dto.getPhoneNumber())
                .setProvider(dto.getProvider())
                .setRole(dto.getRole())
                .setAgreeMarketing(dto.isAgreeMarketing())
                .setAgreeThirdParty(dto.isAgreeThirdParty())
                .build();
    }

    // 로그인 응답 (토큰) dto -> gRPC
    public static TokenResponse toTokenResponse(TokenResponseDTO dto) {
        return TokenResponse.newBuilder()
                .setEmail(dto.getEmail())
                .setAccessToken(dto.getAccessToken())
                .setRefreshToken(dto.getRefreshToken())
                .build();
    }

    // 회원 조회 응답 dto -> gRPC
    public static GetMemberResponse toGetMemberResponse(MemberResponseDTO dto) {
        return GetMemberResponse.newBuilder()
                .setId(dto.getId())
                .setEmail(dto.getEmail())
                .setProvider(dto.getProvider())
                .setRole(dto.getRole())
                .build();
    }

    // 회원 권한 조회 응답 String -> gRPC
    public static GetAuthorityResponse toAuthorityResponse(String authority, Long memberId) {
        return GetAuthorityResponse.newBuilder()
                .setAuthority(authority)
                .setMemberId(memberId)
                .build();
    }

    // 카카오 로그인 키 전송 응답 Map -> gRPC
    public static KakaoConfigResponse toKakaoConfigResponse(Map<String, String> configMap) {
        return KakaoConfigResponse.newBuilder()
                .setClientId(configMap.get("kakaoClientId"))
                .setRedirectUri(configMap.get("kakaoRedirectUri"))
                .build();
    }

    // 네이버 로그인 키 전송 응답 dto -> gRPC
    public static NaverConfigResponse toNaverConfigResponse(NaverConfigResponseDTO dto) {
        return NaverConfigResponse.newBuilder()
                .setClientId(dto.getClientId())
                .setRedirectUri(dto.getRedirectUri())
                .setState(dto.getState())
                .build();
    }

    // 프로필
    public static ProfileResponse toProfileResponse(ProfileResponseDTO dto) {
        return ProfileResponse.newBuilder()
                .setNickname(dto.getNickname())
                .setProfileImg(dto.getProfileImg())
                .build();
    }

    // 프로필사진 변경용 presigned url 응답
    public static PresignedUrlResponse toPresignedUrlResponse(PresignedUrlResponseDTO dto) {
        return PresignedUrlResponse.newBuilder()
                .setPresignedUrl(dto.presignedUrl())
                .setImagePath(dto.actualImagePath())
                .build();
    }

    // 프로필 상세 조회
    public static ProfileDetailResponse toProfileDetailResponse(ProfileDetailResponseDTO dto) {
        return ProfileDetailResponse.newBuilder()
                .setMemberId(dto.memberId())
                .setNickname(dto.nickname())
                .setImageUrl(dto.imageUrl())
                .setScore(dto.score())
                .addAllMainCategory(dto.mainCategory())
                .setAgreeMarketing(dto.agreeMarketing())
                .setAgree3RdParty(dto.agree3rdParty())
                .build();
    }

    // 타 사용자 프로필 조회
    public static GetOtherProfileResponse toOtherProfileResponse(OtherProfileResponseDTO dto) {
        return GetOtherProfileResponse.newBuilder()
                .setMemberId(dto.memberId())
                .setNickname(dto.nickname())
                .setImageUrl(dto.imageUrl())
                .setScore(dto.score())
                .addAllMainCategory(dto.mainCategory())
                .addAllProducts(dto.products().stream().map(MemberGrpcMapper::toSimpleProduct).toList())
                .build();
    }

    private static SimpleProductResponse toSimpleProduct(SimpleProductDTO dto) {
        return SimpleProductResponse.newBuilder()
                .setProductId(dto.productId())
                .setTitle(dto.title())
                .setPrice(dto.price())
                .setImageUrl(dto.imageUrl())
                .build();
    }
}
