package hanium.user_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void createSmsRedis(String phoneNumber, String smsCode) {
        int validTime = 3 * 60; // 3분
        stringRedisTemplate.opsForValue().set(
                // 키: phoneNumber, 값: smsCode
                phoneNumber, smsCode, Duration.ofSeconds(validTime)
        );
    }

    public String getSmsRedisValue(String phoneNumber) {
        return stringRedisTemplate.opsForValue().get(phoneNumber);
    }

    public void deleteSmsRedis(String phoneNumber) {
        stringRedisTemplate.delete(phoneNumber);
    }

    public boolean hasKey(String phoneNumber) {
        return stringRedisTemplate.hasKey(phoneNumber);
    }
}
