package hanium.apigateway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private String nickname;
    private boolean agreeMarketing;
    private boolean agreeThirdParty;
}
