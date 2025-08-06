package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.GetMemberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {
    private Long id;
    private String email;
    private String provider;
    private String role;

    public static MemberResponseDTO from(GetMemberResponse proto) {
        return MemberResponseDTO.builder()
                .id(proto.getId())
                .email(proto.getEmail())
                .provider(proto.getProvider())
                .role(proto.getRole())
                .build();
    }
}