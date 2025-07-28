package hanium.user_service.dto.response;

import hanium.user_service.domain.Member;
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

    public static SignUpResponseDTO from(Member member) {
        return SignUpResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .provider(String.valueOf(member.getProvider()))
                .role(String.valueOf(member.getRole()))
                .agreeMarketing(member.isAgreeMarketing())
                .agreeThirdParty(member.isAgreeThirdParty())
                .build();
    }
}
