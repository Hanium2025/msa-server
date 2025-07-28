package hanium.discovery_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class DiscoveryController {
    @GetMapping("/health-check")
    public String status() {
        return "디스커버리 서비스 정상 작동!";
    }
}
