package hanium.user_service.mapper.grpc;

import hanium.common.proto.user.*;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.LoginResponseDTO;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.dto.response.SignUpResponseDTO;

public class MemberGrpcMapper {

    // SignUpRequest gRPC -> dto
    public static SignUpRequestDTO toSignupDto(SignUpRequest request) {
        return SignUpRequestDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .confirmPassword(request.getConfirmPassword())
                .phoneNumber(request.getPhoneNumber())
                .nickname(request.getNickname())
                .agreeMarketing(request.getAgreeMarketing())
                .agreeThirdParty(request.getAgreeThirdParty())
                .build();
    }

    // SignupResponse dto -> gRPC
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

    // LoginRequest gRPC -> dto
    public static LoginRequestDTO toLoginDto(LoginRequest request) {
        return LoginRequestDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    // LoginResponse dto -> gRPC
    public static LoginResponse toLoginResponse(LoginResponseDTO dto) {
        return LoginResponse.newBuilder()
                .setEmail(dto.getEmail())
                .setToken(dto.getAccessToken())
                .setTokenType(dto.getTokenType())
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
}
