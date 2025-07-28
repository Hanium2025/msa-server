package hanium.user_service.dto.request;

import hanium.common.proto.user.VerifySmsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySmsDTO {
    private String phoneNumber;
    private String smsCode;

    public static VerifySmsDTO from(VerifySmsRequest request) {
        return VerifySmsDTO.builder()
                .phoneNumber(request.getPhoneNumber())
                .smsCode(request.getSmsCode())
                .build();
    }
}
