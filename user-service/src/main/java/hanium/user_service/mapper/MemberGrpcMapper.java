package hanium.user_service.mapper;

import hanium.common.proto.user.GetAuthorityResponse;
import hanium.common.proto.user.GetMemberResponse;
import hanium.common.proto.user.SignUpResponse;
import hanium.common.proto.user.TokenResponse;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.dto.response.SignUpResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;

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
                .setEmail(dto.getEmail())
                .setPhoneNumber(dto.getPhoneNumber())
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
}
