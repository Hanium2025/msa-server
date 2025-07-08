package hanium.user_service.dto.response;

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
    private String phoneNumber;
}
