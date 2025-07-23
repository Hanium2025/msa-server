package hanium.user_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.VerifySmsDTO;
import hanium.user_service.repository.MemberRepository;
import hanium.user_service.repository.SmsRepository;
import hanium.user_service.service.SmsService;
import hanium.user_service.util.CoolSmsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SmsServiceImpl implements SmsService {

    private final CoolSmsUtil coolSmsUtil;
    private final SmsRepository smsRepository;
    private final MemberRepository memberRepository;

    /**
     * 파라미터로 받은 전화번호에 인증번호를 전송합니다.
     * 이미 가입된 번호는 HAS_PHONE 예외를 던집니다.
     * 전송한 인증번호는 Redis에 전화번호-인증번호 형태로 저장됩니다.
     *
     * @param phoneNumber 전화번호
     */
    @Override
    public void sendSms(String phoneNumber) {
        // 이미 가입된 번호 예외 처리
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);
        if (member.isPresent()) {
            throw new CustomException(ErrorCode.HAS_PHONE);
        }
        // 6자리 랜덤 인증번호 생성
        String smsCode = Integer.toString((int)
                (Math.random() * (999999 - 100000 + 1)) + 100000);
        // sms 발송
        coolSmsUtil.send(phoneNumber, smsCode);
        smsRepository.createSmsRedis(phoneNumber, smsCode);
    }

    /**
     * '인증번호 확인'을 요청한 전화번호와 해당 인증번호를 dto로 받아
     * Redis에 해당 전화번호가 키로 존재하고, 이전에 전송된 인증번호가 dto로 전달된 인증번호와 같다면
     * 인증 성공(true)을 반환합니다.
     *
     * @param dto 전화번호, 인증번호
     * @return 인증 성공 여부
     */
    @Override
    public boolean verifyCode(VerifySmsDTO dto) {
        String phoneNumber = dto.getPhoneNumber();
        String smsCode = dto.getSmsCode();
        // 전화번호 키가 존재하고, 이전에 전송된 번호와 입력된 번호 같다면
        if (smsRepository.hasKey(phoneNumber) &&
                smsRepository.getSmsRedisValue(phoneNumber).equals(smsCode)) {
            // 기존 키 삭제
            smsRepository.deleteSmsRedis(phoneNumber);
            return true;
        } else {
            return false;
        }
    }
}
