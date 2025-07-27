package hanium.user_service.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoolSmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecret;
    @Value("${coolsms.number}")
    private String senderNumber;

    DefaultMessageService messageService;

    // 메시지 서비스 초기화
    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(
                apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void send(String receiverNumber, String smsCode) {
        // 메시지 객체 설정
        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(receiverNumber);
        message.setText("[한이음 중고거래] 회원가입 인증번호는 " + smsCode + " 입니다.");
        log.info("✅ CoolSMS 메시지객체 생성됨: {}, {}", receiverNumber, smsCode);
        // 메시지 발송
        messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("✅ CoolSMS 메시지 발송 메서드 호출됨");
    }
}
