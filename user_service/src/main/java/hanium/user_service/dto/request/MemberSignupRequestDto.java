package hanium.user_service.dto.request;

import lombok.Getter;

@Getter
public class MemberSignupRequestDto {
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
}
