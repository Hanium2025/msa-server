package hanium.user_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class MemberController {

    @Value("${jwt.secret:NOT_FOUND}")
    private String jwtSecret;
    @Value("${jwt.access.expiration:-1}")
    private String accessExpiration;
    @Value("${jwt.refresh.expiration:-1}")
    private String refreshExpiration;

    @GetMapping("/health-check")
    public String status() {
        return String.format("""
                        User Service 설정:\s
                        jwt secret = %s
                        jwt access exp = %s
                        jwt refresh exp = %s""",
                jwtSecret, accessExpiration, refreshExpiration);
    }
}
