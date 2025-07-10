package hanium.apigateway_service.mapper;

import hanium.apigateway_service.dto.user.*;
import hanium.common.proto.user.*;

public class UserGrpcMapperForGateway {

    // SignUpRequest dto -> grpc
    public static SignUpRequest toSignUpGrpc(SignUpRequestDTO dto) {
        return SignUpRequest.newBuilder()
                .setEmail(dto.getEmail())
                .setPassword(dto.getPassword())
                .setConfirmPassword(dto.getConfirmPassword())
                .setPhoneNumber(dto.getPhoneNumber())
                .setNickname(dto.getNickname())
                .setAgreeMarketing(dto.isAgreeMarketing())
                .setAgreeThirdParty(dto.isAgreeThirdParty())
                .build();
    }

    // SignUpResponse grpc -> dto
    public static SignUpResponseDTO toSignUpDTO(SignUpResponse response) {
        return SignUpResponseDTO.builder()
                .id(response.getMemberId())
                .email(response.getEmail())
                .phoneNumber(response.getPhoneNumber())
                .provider(response.getProvider())
                .role(response.getRole())
                .agreeMarketing(response.getAgreeMarketing())
                .agreeThirdParty(response.getAgreeThirdParty())
                .build();
    }

    // LoginRequest dto -> grpc
    public static LoginRequest toLoginGrpc(LoginRequestDTO dto) {
        return LoginRequest.newBuilder()
                .setEmail(dto.getEmail())
                .setPassword(dto.getPassword())
                .build();
    }

    // LoginResponse grpc -> dto
    public static LoginResponseDTO toLoginDTO(LoginResponse response) {
        return LoginResponseDTO.builder()
                .email(response.getEmail())
                .accessToken(response.getToken())
                .tokenType(response.getTokenType())
                .build();
    }

    // 회원 조회 응답 grpc -> dto
    public static MemberResponseDTO toMemberDTO(GetMemberResponse response) {
        return MemberResponseDTO.builder()
                .email(response.getEmail())
                .phoneNumber(response.getPhoneNumber())
                .provider(response.getProvider())
                .role(response.getRole())
                .build();
    }
}
