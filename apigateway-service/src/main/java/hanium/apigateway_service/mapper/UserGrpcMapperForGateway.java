package hanium.apigateway_service.mapper;

import hanium.apigateway_service.dto.LoginRequestDTO;
import hanium.apigateway_service.dto.LoginResponseDTO;
import hanium.apigateway_service.dto.SignUpRequestDTO;
import hanium.common.proto.user.LoginRequest;
import hanium.common.proto.user.LoginResponse;
import hanium.common.proto.user.SignUpRequest;

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
}
