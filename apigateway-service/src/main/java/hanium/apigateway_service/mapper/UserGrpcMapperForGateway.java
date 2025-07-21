package hanium.apigateway_service.mapper;

import hanium.apigateway_service.dto.user.request.LoginRequestDTO;
import hanium.apigateway_service.dto.user.request.SignUpRequestDTO;
import hanium.apigateway_service.dto.user.request.VerifySmsRequestDTO;
import hanium.common.proto.user.LoginRequest;
import hanium.common.proto.user.SignUpRequest;
import hanium.common.proto.user.VerifySmsRequest;

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

    // VerifySmsRequest dto -> grpc
    public static VerifySmsRequest toVerifySmsGrpc(VerifySmsRequestDTO dto) {
        return VerifySmsRequest.newBuilder()
                .setPhoneNumber(dto.getPhoneNumber())
                .setSmsCode(dto.getSmsCode())
                .build();
    }
}
