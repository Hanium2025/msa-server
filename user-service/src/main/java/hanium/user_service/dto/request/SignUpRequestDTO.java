package hanium.user_service.dto.request;

import hanium.common.proto.user.SignUpRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private String nickname;
    private boolean agreeMarketing;
    private boolean agreeThirdParty;

    public boolean getAgreeMarketing() {
        return agreeMarketing;
    }

    public boolean getAgreeThirdParty() {
        return agreeThirdParty;
    }

    public static SignUpRequestDTO from(SignUpRequest proto) {
        return SignUpRequestDTO.builder()
                .email(proto.getEmail())
                .password(proto.getPassword())
                .confirmPassword(proto.getConfirmPassword())
                .phoneNumber(proto.getPhoneNumber())
                .nickname(proto.getNickname())
                .agreeMarketing(proto.getAgreeMarketing())
                .agreeThirdParty(proto.getAgreeThirdParty())
                .build();
    }
}
