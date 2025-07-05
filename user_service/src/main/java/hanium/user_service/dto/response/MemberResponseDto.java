package hanium.user_service.dto.response;

import lombok.Builder;

@Builder
public class MemberResponseDto {
    private Long id;
    private String email;
    private String phoneNumber;
}
