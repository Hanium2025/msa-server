package hanium.apigateway_service.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySmsRequestDTO {
    private String phoneNumber;
    private String smsCode;
}
