package hanium.user_service.mapper.grpc;

import hanium.common.proto.user.LoginRequest;
import hanium.common.proto.user.LoginResponse;
import hanium.common.proto.user.SignUpRequest;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.LoginResponseDTO;

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
}
