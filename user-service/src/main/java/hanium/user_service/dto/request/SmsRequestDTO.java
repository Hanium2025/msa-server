package hanium.user_service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmsRequestDTO {
    private String phoneNumber;
}
