package hanium.apigateway_service.mapper;

import hanium.apigateway_service.dto.MemberSignupRequestDTO;
import hanium.common.proto.user.SignUpRequest;

public class UserGrpcMapperForGateway {

    public static SignUpRequest toSignUpGrpc(MemberSignupRequestDTO dto) {
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
}
