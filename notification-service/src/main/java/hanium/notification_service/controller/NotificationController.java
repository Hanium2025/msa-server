package hanium.notification_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class NotificationController {
    @GetMapping("/health-check")
    public String status() {
        return "알림 서비스 정상 작동";
    }

}
