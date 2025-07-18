package hanium.apigateway_service.dto.user.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class smsRequestDTO {
    @NotEmpty(message = "휴대폰 번호를 입력해주세요.")
    private String phoneNumber;
}
