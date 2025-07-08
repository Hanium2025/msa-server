package hanium.user_service.mapper.grpc;

import hanium.common.proto.user.SignUpRequest;
import hanium.user_service.dto.request.SignUpRequestDTO;

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
}
