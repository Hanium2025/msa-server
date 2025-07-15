package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.SignUpResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDTO {

    private Long id;
    private String email;
    private String phoneNumber;
    private String provider;
    private String role;
    private boolean agreeMarketing;
    private boolean agreeThirdParty;

    public static SignUpResponseDTO from(SignUpResponse proto) {
        return SignUpResponseDTO.builder()
                .id(proto.getMemberId())
                .email(proto.getEmail())
                .phoneNumber(proto.getPhoneNumber())
                .provider(proto.getProvider())
                .role(proto.getRole())
                .agreeMarketing(proto.getAgreeMarketing())
                .agreeThirdParty(proto.getAgreeThirdParty())
                .build();
    }
}