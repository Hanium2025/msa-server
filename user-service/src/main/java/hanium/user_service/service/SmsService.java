package hanium.user_service.service;

import hanium.user_service.dto.request.VerifySmsDTO;

public interface SmsService {

    void sendSms(String phoneNumber);

    boolean verifyCode(VerifySmsDTO dto);
}
