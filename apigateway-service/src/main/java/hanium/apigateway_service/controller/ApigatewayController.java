package hanium.apigateway_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ApigatewayController {
    @GetMapping("/health-check")
    public String status() {
        return "apigateway 서비스 정상 작동";
    }
}
