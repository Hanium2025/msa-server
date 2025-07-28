package hanium.user_service.dto.response;

import hanium.user_service.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberResponseDTO {

    private String email;
    private String phoneNumber;
    private String provider;
    private String role;

    public static MemberResponseDTO from(Member member) {
        return MemberResponseDTO.builder()
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .provider(String.valueOf(member.getProvider()))
                .role(String.valueOf(member.getRole()))
                .build();
    }
}
