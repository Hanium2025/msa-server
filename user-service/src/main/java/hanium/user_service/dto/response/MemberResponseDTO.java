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
    private Long id;
    private String email;
    private String provider;
    private String role;

    public static MemberResponseDTO from(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .provider(String.valueOf(member.getProvider()))
                .role(String.valueOf(member.getRole()))
                .build();
    }
}
