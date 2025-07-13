package hanium.apigateway_service.dto.user;

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
}
